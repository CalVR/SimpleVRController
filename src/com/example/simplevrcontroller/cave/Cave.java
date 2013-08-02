package com.example.simplevrcontroller.cave;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.example.simplevrcontroller.SettingsManager;
import com.example.simplevrcontroller.networking.NetworkManager;
import com.example.simplevrcontroller.networking.location.WirelessLocation;

public class Cave {

	private String address;
	private String name;
	private WirelessLocation wl;
	private int presetPort, gamepadPort;
	private int defaultPreset;
	private boolean startGamepadOnConnect;
	private int maxSpeed;

	/**
	 * Creates a new cave with the given information.
	 * 
	 * @param name
	 *            The Name for the Cave
	 * @param address
	 *            The address of the cave (can be an IP or anything you can make
	 *            with )
	 * @param presetPort
	 *            Port to connect to for the preset server (aka FuturePatient)
	 * @param gamepadPort
	 *            Port to connect to for the Gamepad server (AndroidNavigator)
	 * @param defaultPreset
	 *            Default preset to send on connect (will NOT be sent
	 *            automatically)
	 * @param startGamepadOnConnect
	 */
	public Cave(String name, String address, int presetPort, int gamepadPort) {

		this.startGamepadOnConnect = false;
		;
		this.address = address;
		this.name = name;
		this.presetPort = presetPort;
		this.gamepadPort = gamepadPort;
		this.defaultPreset = -1;
		this.maxSpeed = 200;

		wl = new WirelessLocation("location");
		wl.setCave(this);
	}

	/**
	 * Creates a new Cave based off the information in the given element.
	 * 
	 * @param n
	 *            The element to read.
	 */
	public Cave(Element n) {
		name = n.getAttribute("name");
		address = n.getAttribute("address");

		// Get port for the preset server (FuturePatient)
		try {
			presetPort = Integer.parseInt(n.getAttribute("presetPort"));
		} catch (NumberFormatException e) {
			presetPort = NetworkManager.DEFAULT_PRESET_SERVER_PORT;
		}

		// Get port for GamePad (AndroidNavigator)
		try {
			gamepadPort = Integer.parseInt(n.getAttribute("gamepadPort"));
		} catch (NumberFormatException e) {
			gamepadPort = NetworkManager.DEFAULT_GAMEPAD_SERVER_PORT;
		}

		// Get the default preset
		try {
			defaultPreset = Integer.parseInt(n.getAttribute("default"));
		} catch (NumberFormatException e) {
			defaultPreset = -1; // No default
		}

		// Get the max speed
		try {
			maxSpeed = Integer.parseInt(n.getAttribute("maxSpeed"));
		} catch (NumberFormatException e) {
			maxSpeed = SettingsManager.DEFAULT_MAX_SPEED; // No default
		}

		// Get whether to start the Gamepad when connected
		if (n.getAttribute("startGamepadOnConnect").equals("true"))
			startGamepadOnConnect = true;
		else
			startGamepadOnConnect = false;

		NodeList elements = n.getElementsByTagName("location");
		for (int y = 0; y < elements.getLength(); y++) {
			wl = new WirelessLocation((Element) elements.item(y));
			wl.setCave(this);
		}

		if (wl == null) {
			wl = new WirelessLocation("location");
			wl.setCave(this);
		}

	}

	/**
	 * Sets the tablet's current location to be the location for this Cave
	 * 
	 * @param net
	 *            The NetworkManager to read.
	 */
	public void setWirelessLocation(NetworkManager net) {
		wl.setLocation(net);
	}

	/**
	 * Gets this Cave's location
	 * 
	 * @return A WirelessLocation
	 */
	public WirelessLocation getWirelessLocation() {
		return wl;
	}

	/**
	 * Gets the address of this Cave, which can either be an IP or a domain.
	 * 
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the address of this cave. Can either be an IP or a domain.
	 * 
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Gets the name of this Cave.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Writes this cave's data to the given element.
	 * 
	 * @param base
	 *            Element to write to (it better not be null)
	 */
	public void writeToElement(Element base) {

		base.setAttribute("name", name);
		base.setAttribute("address", address);
		base.setAttribute("maxSpeed", "" + maxSpeed);
		base.setAttribute("presetPort", "" + presetPort);
		base.setAttribute("gamepadPort", "" + gamepadPort);
		base.setAttribute("startGamepadOnConnect", "" + startGamepadOnConnect);
		base.setAttribute("default", "" + defaultPreset);
		Element locEl = base.getOwnerDocument().createElement("location");
		wl.writeToElement(locEl);
		base.appendChild(locEl);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Cave))
			return false;

		return name.equals(((Cave) o).getName());
	}

	/**
	 * @return This cave's port for the Preset server (FuturePatient)
	 */
	public int getPresetServerPort() {
		return presetPort;
	}

	/**
	 * @return This cave's port for the Gamepad server (AndroidNavigator)
	 */
	public int getGamepadServerPort() {
		return gamepadPort;
	}

	/**
	 * Sets the port that this Cave connects to the Preset server with.
	 * 
	 * @param port
	 *            the port to set
	 */
	public void setPresetServerPort(int port) {
		this.presetPort = port;
	}

	/**
	 * Sets the port that this Cave connects to the Gamepad server with.
	 * 
	 * @param port
	 *            the port to set
	 */
	public void setGamepadServerPort(int port) {
		this.gamepadPort = port;
	}

	/**
	 * @return the defaultPreset
	 */
	public int getDefaultPreset() {
		return defaultPreset;
	}

	/**
	 * @param defaultPreset
	 *            the defaultPreset to set
	 */
	public void setDefaultPreset(int defaultPreset) {
		this.defaultPreset = defaultPreset;
	}

	/**
	 * Gets whether the Gamepad should be started when connecting to this Cave
	 * 
	 * @return True if it should be started, false if it shouldn't
	 */
	public boolean getStartGamepadOnConnect() {
		return startGamepadOnConnect;
	}

	/**
	 * Sets whether the Gamepad should be started when connecting to this cave.
	 * 
	 * @param b
	 *            True if it should be started, false if it shouldn't
	 */
	public void setStartGamepadOnConnect(boolean b) {
		startGamepadOnConnect = b;
	}

	public void setMaxSpeed(int speed) {
		maxSpeed = speed;

	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

}
