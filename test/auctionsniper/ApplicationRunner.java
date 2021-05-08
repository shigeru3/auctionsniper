package auctionsniper;

import auctionsniper.ui.Main;
import auctionsniper.ui.MainWindow;

public class ApplicationRunner {
	public static final String SNIPER_XMPP_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	private String STATUS_JOINING = "Joining";
	private String STATUS_LOST = "Lost";
	private String STATUS_BIDDING = "Bidding";
	private String STATUS_WINNING = "Winning";
	private String STATUS_WON = "Won";
	private AuctionSniperDriver driver;
	private String itemId;

	public void startBiddingIn(final FakeAuctionServer auction) {
		itemId = auction.getItemId();
		Thread thread = new Thread("Test Application") {
			@Override
			public void run() {
				try {
					Main.main(FakeAuctionServer.XMPP_HOSTNAME, SNIPER_XMPP_ID, SNIPER_PASSWORD, auction.getItemId());
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
		//driver.showsSniperStatus(itemId, 0, 0, STATUS_JOINING);
	}

	public void showsSniperHasLostAuction() {
		driver.showsSniperStatus(itemId, 0, 0, STATUS_LOST);
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}

	public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
		driver.showsSniperStatus(itemId, lastPrice, lastBid, STATUS_BIDDING);
	}

	public void hasShownSniperIsWinning(int winningBid) {
		driver.showsSniperStatus(itemId, winningBid, winningBid, STATUS_WINNING);
	}

	public void showsSniperHasWonAuction(int lastPrice) {
		driver.showsSniperStatus(itemId, lastPrice, lastPrice, STATUS_WON);
	}
}
