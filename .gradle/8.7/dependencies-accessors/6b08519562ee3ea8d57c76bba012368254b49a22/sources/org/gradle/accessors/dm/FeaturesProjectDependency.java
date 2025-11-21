package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.internal.artifacts.dependencies.ProjectDependencyInternal;
import org.gradle.api.internal.artifacts.DefaultProjectDependencyFactory;
import org.gradle.api.internal.artifacts.dsl.dependencies.ProjectFinder;
import org.gradle.api.internal.catalog.DelegatingProjectDependency;
import org.gradle.api.internal.catalog.TypeSafeProjectDependencyFactory;
import javax.inject.Inject;

@NonNullApi
public class FeaturesProjectDependency extends DelegatingProjectDependency {

    @Inject
    public FeaturesProjectDependency(TypeSafeProjectDependencyFactory factory, ProjectDependencyInternal delegate) {
        super(factory, delegate);
    }

    /**
     * Creates a project dependency on the project at path ":features:dashboard"
     */
    public Features_DashboardProjectDependency getDashboard() { return new Features_DashboardProjectDependency(getFactory(), create(":features:dashboard")); }

    /**
     * Creates a project dependency on the project at path ":features:diagnostics"
     */
    public Features_DiagnosticsProjectDependency getDiagnostics() { return new Features_DiagnosticsProjectDependency(getFactory(), create(":features:diagnostics")); }

    /**
     * Creates a project dependency on the project at path ":features:logs"
     */
    public Features_LogsProjectDependency getLogs() { return new Features_LogsProjectDependency(getFactory(), create(":features:logs")); }

    /**
     * Creates a project dependency on the project at path ":features:performance"
     */
    public Features_PerformanceProjectDependency getPerformance() { return new Features_PerformanceProjectDependency(getFactory(), create(":features:performance")); }

}
