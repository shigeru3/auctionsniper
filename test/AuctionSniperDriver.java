import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.*;
import com.objogate.wl.swing.gesture.GesturePerformer;
import auctionsniper.ui.MainWindow;

import static org.hamcrest.CoreMatchers.equalTo;

public class AuctionSniperDriver extends JFrameDriver {
	public AuctionSniperDriver(int timeoutMills) {
		super(new GesturePerformer(),
				JFrameDriver.topLevelFrame(
						named(MainWindow.MAIN_WINDOW_NAME),
						showingOnScreen()),
				new AWTEventQueueProber(timeoutMills, 100));
	}

	public void showsSniperStatus(String statusText) {
		new JLabelDriver(
				this, named(MainWindow.SNIPER_STATUS_NAME)
		).hasText(equalTo(statusText));
	}
}
