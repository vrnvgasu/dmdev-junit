package ru.edu;

import java.io.PrintWriter;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

public class TestLauncher {

	public static void main(String[] args) {
		Launcher launcher = LauncherFactory.create();

		// в процессе запуска тестов можно подключать много встроенных листнеров (можно и свои делать)
		// SummaryGeneratingListener - дает статистику по тестам
		var summaryGeneratingListener = new SummaryGeneratingListener();
		// можно сразу в Launcher подключать, но мы подключим в конце в execute
//		launcher.registerTestExecutionListeners(summaryGeneratingListener);

		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
				.request()
				// где будем искать тесты. Несколько вариантов:
//				.selectors(DiscoverySelectors.selectClass(UserServiceTest.class))
				.selectors(DiscoverySelectors.selectPackage("ru.edu.service"))
				.build();

		// запускаем тесты из request
		launcher.execute(request, summaryGeneratingListener);

		// отобразили статистику в консоле
		try(var writer = new PrintWriter(System.out)) {
			summaryGeneratingListener.getSummary().printTo(writer);
		}
	}

}
