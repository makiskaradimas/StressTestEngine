package engine.tasks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import engine.stepdefs.tasks.annotations.WhenStressTask;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author ekaradimas
 * @since 19/2/2015
 */
@Component
public class TaskFactory {

	/** Set of tasks */
	private Map<String, String> annotated;

	@Value("${tasks.package}")
	private String scanPackage;

	@PostConstruct
	public void initTaskFactory() {

		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);

		scanner.addIncludeFilter(new AnnotationTypeFilter(WhenStressTask.class));
		annotated = new HashMap<String, String>();
		for (BeanDefinition bd : scanner.findCandidateComponents(scanPackage)) {
			try {
				annotated.put(Class.forName(bd.getBeanClassName()).getAnnotation(WhenStressTask.class).value(),
						bd.getBeanClassName());
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}

	}

	public Map<String, String> getAnnotated() {
		return annotated;
	}
}
