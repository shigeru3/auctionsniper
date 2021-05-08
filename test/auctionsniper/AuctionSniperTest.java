package auctionsniper;

import auctionsniper.AuctionEventListener.PriceSource;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import static auctionsniper.SniperState.*;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(JMock.class)
public class AuctionSniperTest {
	private final Mockery context = new Mockery();
	private final Auction auction = context.mock(Auction.class);
	private final SniperListener sniperListener = context.mock(SniperListener.class);
	private final String ITEM_ID = "auction-item-54321";
	private final AuctionSniper sniper = new AuctionSniper(ITEM_ID, auction, sniperListener);
	private final States sniperState = context.states("sniper");

	@Test
	public void reportsLostIfAuctionClosesImmediately() {
		context.checking(new Expectations() {{
			oneOf(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
		}});
		sniper.auctionClosed();
	}

	@Test
	public void reportLostIfAuctionClosesWhenBidding() {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
				then(sniperState.is("bidding"));

			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
				when(sniperState.is("bidding"));
		}});
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
	}

	private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
		return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is ", "was") {
			@Override
			protected SniperState featureValueOf(SniperSnapshot actual) {
				return actual.state;
			}
		};
	}

	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
		final int price = 1001;
		final int increment = 25;
		final int bid = price + increment;

		context.checking(new Expectations() {{
			oneOf(auction).bid(bid);
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
		}});
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
	}

	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
				then(sniperState.is("bidding"));
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
				when(sniperState.is("bidding"));
		}});
		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
		sniper.currentPrice(123, 45, PriceSource.FromSniper);
	}

	@Test
	public void reportsWonIfAuctionClosesWhenWinning() {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
				then(sniperState.is("winning"));

			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(WON)));
				when(sniperState.is("winning"));
		}});

		sniper.currentPrice(123, 45, PriceSource.FromSniper);
		sniper.auctionClosed();
	}
}
