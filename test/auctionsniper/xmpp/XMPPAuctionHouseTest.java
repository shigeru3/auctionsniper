package auctionsniper.xmpp;

import auctionsniper.ApplicationRunner;
import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.FakeAuctionServer;
import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;
import static org.junit.Assert.assertTrue;

public class XMPPAuctionHouseTest {
	private XMPPAuctionHouse auctionHouse;
	private FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");

	@Before
	public void openConnection() throws XMPPException {
		auctionHouse = XMPPAuctionHouse.connect(XMPP_HOSTNAME, ApplicationRunner.SNIPER_XMPP_ID, ApplicationRunner.SNIPER_PASSWORD);
	}

	@Before
	public void startAuction() throws XMPPException {
		auctionServer.startSellingItem();
	}

	@After
	public void closeConnection() {
		auctionHouse.disconnect();
	}

	@After
	public void stopAuction() {
		auctionServer.stop();
	}

	@Test
	public void receiveEventsFromAuctionServerAfterJoining() throws Exception {
		CountDownLatch auctionWasClosed = new CountDownLatch(1);
		Auction auction = auctionHouse.auctionFor("item-54321");
		auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

		auction.join();
		auctionServer.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		auctionServer.announceClosed();

		assertTrue("should have been closed", auctionWasClosed.await(2, TimeUnit.SECONDS));
	}

	private AuctionEventListener auctionClosedListener(CountDownLatch auctionWasClosed) {
		return new AuctionEventListener() {
			@Override
			public void auctionClosed() {
				auctionWasClosed.countDown();
			}

			@Override
			public void currentPrice(int price, int increment, PriceSource priceSource) {

			}
		};
	}
}
