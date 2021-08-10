package auctionsniper;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.*;
import com.objogate.wl.swing.gesture.GesturePerformer;
import auctionsniper.ui.MainWindow;

import javax.swing.*;
import javax.swing.table.JTableHeader;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static java.lang.String.valueOf;

public class AuctionSniperDriver extends JFrameDriver {
	@SuppressWarnings("unchecked")
	public void startBiddingFor(String itemId, int stopPrice) {
		textField(MainWindow.NEW_ITEM_ID_NAME).replaceAllText(itemId);
		textField(MainWindow.NEW_ITEM_STOP_PRICE_NAME).replaceAllText(String.valueOf(stopPrice));
		bidButton().click();
	}

	private JButtonDriver bidButton() {
		return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
	}

	private JTextFieldDriver itemIdField() {
		JTextFieldDriver newItemId = new JTextFieldDriver(this, JTextField.class, named(MainWindow.NEW_ITEM_ID_NAME));
		newItemId.focusWithMouse();
		return newItemId;
	}

	public AuctionSniperDriver(int timeoutMills) {
		super(new GesturePerformer(),
				JFrameDriver.topLevelFrame(
						named(MainWindow.MAIN_WINDOW_NAME),
						showingOnScreen()),
				new AWTEventQueueProber(timeoutMills, 100));
	}

	public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
		JTableDriver table = new JTableDriver(this);
		table.hasRow(
				matching(withLabelText(itemId), withLabelText(valueOf(lastPrice)), withLabelText(valueOf(lastBid)), withLabelText(statusText))
		);
	}

	public void hasColumnTitles() {
		JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
		headers.hasHeaders(matching(withLabelText("Item"), withLabelText("Last Price"), withLabelText("Last Bid"), withLabelText("State")));
	}

	private JTextFieldDriver textField(
			String fieldName) {
		JTextFieldDriver fd = new JTextFieldDriver(this, JTextField.class, named(fieldName));
		fd.focusWithMouse();
		return fd;
	}
}
