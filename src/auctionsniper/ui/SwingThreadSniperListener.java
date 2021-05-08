package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;

import javax.swing.*;

public class SwingThreadSniperListener implements SniperListener {
	private MainWindow ui;

	public SwingThreadSniperListener(MainWindow ui) {
		this.ui = ui;
	}

	@Override
	public void sniperStateChanged(SniperSnapshot snapshot) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ui.sniperStatusChanged(snapshot);
			}
		});
	}
}
