package auctionsniper;

import auctionsniper.ui.SwingThreadSniperListener;

import java.util.ArrayList;

public class SniperLauncher implements UserRequestListener {
	private final ArrayList<Auction> notToBeGCd = new ArrayList<>();
	private final AuctionHouse auctionHouse;
	private final SniperCollector collector;

	public SniperLauncher(AuctionHouse auctionHouse, SniperCollector snipers) {
		this.auctionHouse = auctionHouse;
		this.collector = snipers;
	}

	@Override
	public void joinAuction(String itemId) {
		Auction auction = auctionHouse.auctionFor(itemId);
		notToBeGCd.add(auction);
		AuctionSniper sniper = new AuctionSniper(itemId, auction);
		auction.addAuctionEventListener(sniper);
		auction.join();
	}
}
