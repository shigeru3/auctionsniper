package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import com.objogate.exception.Defect;
import org.jivesoftware.smack.XMPPConnection;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
	private List<SniperSnapshot> snapshots = new ArrayList<>();
	private static String[] STATUS_TEXT = {
			"Joining", "Bidding", "Winning", "Lost", "Won"
	};

	@Override
	public int getRowCount() {
		return snapshots.size();
	}

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public String getColumnName(int column) {
		return Column.at(column).name;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
	}

	public void sniperStateChanged(SniperSnapshot newSnapshot) {
		int row = rowMatching(newSnapshot);
		snapshots.set(row, newSnapshot);
		fireTableRowsUpdated(row, row);
	}

	private int rowMatching(SniperSnapshot snapshot) {
		for (int i = 0;  i < snapshots.size(); i++) {
			if (snapshot.ifForSameItemAs(snapshots.get(i))) {
				return i;
			}
		}
		throw new Defect("Cannot find match for " + snapshot);
	}

	private void joinAuction(XMPPConnection connection, String itemId) throws Exception {
		safelyAddItemToModel(itemId);
	}

	private void safelyAddItemToModel(final String itemId) throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				snapshots.add(SniperSnapshot.joining(itemId));
			}
		});
	}

	public void addSniper(SniperSnapshot snapshot) {
		snapshots.add(snapshot);
	}

	public static String textFor(SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}
}
