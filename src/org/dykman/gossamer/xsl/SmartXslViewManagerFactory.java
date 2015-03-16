package org.dykman.gossamer.xsl;

import org.dykman.gossamer.core.ViewManager;
import org.dykman.gossamer.device.ClientDeviceProfile;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SmartXslViewManagerFactory implements ApplicationContextAware {

	protected ApplicationContext applicationContext;

	public ViewManager createViewManager(ClientDeviceProfile profile) {
		return new SmartXslViewManager(profile, applicationContext);
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
