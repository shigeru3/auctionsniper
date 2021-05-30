package auctionsniper.ui;

import auctionsniper.*;
import auctionsniper.xmpp.XMPPAuctionHouse;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class Main {
	@SuppressWarnings("unused") private List<Auction> notTobeGCd = new ArrayList<Auction>();
	private static final int ARG_HOSTNAME = 0;
	private static final int ARG_USERNAME = 1;
	private static final int ARG_PASSWORD = 2;

	public static final String AUCTION_RESOURCE = "Auction";

	public static final String JOIN_COMMAND_FORMAT = "%s@host.docker.internal/Auction";
	public static final String BID_COMMAND_FORMAT = "bid command format";

	private final SnipersTableModel snipers = new SnipersTableModel();
	private MainWindow ui;

	private Main() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				ui = new MainWindow(snipers);
			}
		});
	}

	public static void main(String... args) throws Exception {
		Main main = new Main();
		XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
		main.disconnectWhenUICloses(auctionHouse);
		main.addUserRequestListenerFor(auctionHouse);
	}

	private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
		ui.addUserRequestListener(new UserRequestListener() {
			@Override
			public void joinAuction(String itemId) {
				snipers.addSniper(SniperSnapshot.joining(itemId));
				Auction auction = auctionHouse.auctionFor(itemId);
				notTobeGCd.add(auction);
				auction.addAuctionEventListener(
						new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers))
				);
				auction.join();
			}
		});
	}

	private void disconnectWhenUICloses(XMPPAuctionHouse auctionHouse) {
		ui.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				auctionHouse.disconnect();
			}
		});
	}
}
