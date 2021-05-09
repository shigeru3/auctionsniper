package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;

import javax.swing.*;

public class SwingThreadSniperListener implements SniperListener {
	private SniperListener listener;

	public SwingThreadSniperListener(SniperListener listener) {
		this.listener = listener;
	}

	@Override
	public void sniperStateChanged(SniperSnapshot snapshot) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				listener.sniperStateChanged(snapshot);
			}
		});
	}
}
