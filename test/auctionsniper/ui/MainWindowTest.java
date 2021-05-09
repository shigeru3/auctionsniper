package auctionsniper.ui;

import auctionsniper.AuctionSniperDriver;
import auctionsniper.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class MainWindowTest {
	private final SnipersTableModel tableModel = new SnipersTableModel();
	private final MainWindow mainWindow = new MainWindow(tableModel);
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

	@Test
	public void makesUserRequestWhenButtonClicked() {
		final ValueMatcherProbe<String> buttonProne = new ValueMatcherProbe<String>(equalTo("an item-id"), "join request");

		mainWindow.addUserRequestListener(
				new UserRequestListener() {
					public void joinAuction(String itemId) {
						buttonProne.setReceivedValue(itemId);
					}
				}
		);
		driver.startBiddingFor("an item-id");
		driver.check(buttonProne);
	}



}
