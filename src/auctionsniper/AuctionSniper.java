package auctionsniper;


public class AuctionSniper implements AuctionEventListener {
	private SniperListener sniperListener;
	private final Auction auction;
	private SniperSnapshot snapshot;

	public AuctionSniper(String itemId, Auction auction) {
		this.auction = auction;
		this.snapshot = SniperSnapshot.joining(itemId);
	}

	@Override
	public void auctionClosed() {
		snapshot = snapshot.closed();
		notifyChange();
	}

	private void notifyChange() {
		sniperListener.sniperStateChanged(snapshot);
	}

	@Override
	public void currentPrice(int price, int increment, PriceSource priceSource) {
		switch (priceSource) {
			case FromSniper:
				snapshot = snapshot.winning(price);
				break;
			case FromOtherBidder:
				final int bid = price + increment;
				auction.bid(bid);
				snapshot = snapshot.bidding(price, bid);
				break;
		}
		notifyChange();
	}

	public void addSniperListener(SniperListener listener) {
		this.sniperListener = listener;
	}

	public SniperSnapshot getSnapshot() {
		return snapshot;
	}
}
