package auctionsniper;

import auctionsniper.ui.Main;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

import static auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;
import static auctionsniper.SniperState.*;
import static auctionsniper.ui.SnipersTableModel.textFor;

public class ApplicationRunner {
	public static final String SNIPER_XMPP_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	private AuctionSniperDriver driver;

	public void startBiddingIn(final FakeAuctionServer... auctions) {
		startSniper();

		for (FakeAuctionServer auction : auctions) {
			driver.showsSniperStatus(auction.getItemId(), 0, 0, textFor(JOINING));
		}
	}

	private void startSniper() {
		Thread thread = new Thread("Test Application") {
			@Override
			public void run() {
				try {
					Main.main(arguments());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();

		driver = new AuctionSniperDriver(1000);
		driver.hasTitle(MainWindow.APPLICATION_TITLE);
		driver.hasColumnTitles();
	}

	protected static String[] arguments(FakeAuctionServer... auctions) {
		String[] arguments = new String[auctions.length + 3];
		arguments[0] = XMPP_HOSTNAME;
		arguments[1] = SNIPER_XMPP_ID;
		arguments[2] = SNIPER_PASSWORD;
		for (int i = 0; i < auctions.length; i++) {
			arguments[i + 3] = auctions[i].getItemId();
		}
		return arguments;
	}

	public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), 0, 0, textFor(LOST));
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}

	public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(BIDDING));
	}

	public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
		driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(WINNING));
	}

	public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, textFor(WON));
	}

	public void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
		startSniper();
		openBiddingFor(auction, stopPrice);
	}

	private void openBiddingFor(FakeAuctionServer auction, int stopPrice) {
		final String itemId = auction.getItemId();
		driver.startBiddingFor(itemId, stopPrice);
		driver.showsSniperStatus(itemId, 0, 0, SnipersTableModel.textFor(JOINING));
	}

	public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, SnipersTableModel.textFor(SniperState.LOSING));
	}
}
