package soracom_lte_m_button_lambda;

import java.util.Map;

public class ClickEvent {

	public DeviceEvent deviceEvent;
	public DeviceInfo deviceInfo;
	public PlacementInfo placementInfo;

	public static class DeviceEvent {
		public ButtonClicked buttonClicked;
	}

	public static class ButtonClicked {
		public ClickType clickType;
		public String reportedTime;

	}

	public enum ClickType {
		SINGLE, DOUBLE, LONG
	}

	public static class DeviceInfo {
		public String deviceId;
		public String type;
		public Double remainingLife;
		public Map<String, String> attributes;
	}

	public static class PlacementInfo {
		public String projectName;
		public String placementName;
		public Map<String, String> attributes;
		public Map<String, String> devices;
	}
}
