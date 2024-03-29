/*
 * Copyright 2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gmetrics.ant

import org.apache.tools.ant.Task
import org.apache.log4j.Logger
import org.gmetrics.metricset.MetricSet
import org.apache.tools.ant.BuildException
import org.apache.tools.ant.types.FileSet
import org.gmetrics.metricset.DefaultMetricSet
import org.gmetrics.GMetricsRunner
import org.gmetrics.analyzer.SourceAnalyzer
import org.gmetrics.metricset.GroovyDslMetricSet

/**
 * Ant Task for GMetrics.
 * <p/>
 * The <code>metricSetFile</code> property specifies the path to a Groovy MetricSet
 * definition file. By default, the path specified is relative to the classpath, but it may be optionally
 * prefixed by any of the valid java.net.URL prefixes, such as "file:" (to load from a relative or absolute 
 * path on the filesystem), or "http:". The <code>metricSetFile</code> property is optional. If not
 * specified, it uses the set of <code>Metric</code>s defined by <code>DefaultMetricSet</code>.
 * <p/>
 * At least one nested <code>fileset</code> element is required, and is used to specify the source files
 * to be analyzed. This is the standard Ant <i>FileSet</i>, and is quite powerful and flexible.
 * See the <i>Apache Ant Manual</i> for more information on <i>FileSets</i>.
 * <p/>
 * The <code>report</code> nested element defines the type and report-specific options for the
 * output report. The <ode>report</code> element includes a <code>type</code> attribute (which
 * specifies the fully-qualified class name of the <code>ReportWriter</code> class) and
 * can contain nested <code>option</code> elements. Each <code>option</code>, in turn, must
 * include a <code>name</code> and <code>value</code> attribue. Currently, the
 * <code>BasicHtmlReportWriter</code> class (org.gmetrics.report.BasicHtmlReportWriter) is the
 * only report type provided.
 *
 * @see "http://ant.apache.org/manual/index.html"
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class GMetricsTask extends Task {
    private static final LOG = Logger.getLogger(GMetricsTask)

    String metricSetFile

    protected List reportWriters = []
    protected List fileSets = []

    // Abstract creation of the GMetricsNarcRunner instance to allow substitution of test spy for unit tests
    protected createGMetricsRunner = { return new GMetricsRunner() }

    /**
     * Execute this Ant Task
     */
    void execute() throws BuildException {
        assert fileSets
        try {
            def sourceAnalyzer = createSourceAnalyzer()
            def gMetricsRunner = createGMetricsRunner()
            gMetricsRunner.metricSet = createMetricSet()
            gMetricsRunner.reportWriters = reportWriters
            gMetricsRunner.sourceAnalyzer = sourceAnalyzer
            gMetricsRunner.execute()
        }
        catch(Throwable t) {
            LOG.error("GMetricsTask error", t)
            throw t
        }
    }

    /**
     * Ant-defined method (by convention), called with each instance of a nested <fileset>
     * element within this task.
     */
    void addFileset(FileSet fileSet) {
        assert fileSet
        this.fileSets << fileSet
    }

    /**
     * Ant-defined method (by convention), called with each instance of a nested <report>
     * element within this task.
     */
    void addConfiguredReport(Report report) {
        if (!report.type) {
            throw new BuildException("Report type null or empty")
        }
        def reportClass = getClass().classLoader.loadClass(report.type)
        def reportWriter = reportClass.newInstance()
        report.options.each { name, value -> reportWriter[name] = value }
        LOG.debug("Adding report: $reportWriter")
        reportWriters << reportWriter
    }

    private MetricSet createMetricSet() {
        return metricSetFile ? new GroovyDslMetricSet(metricSetFile): new DefaultMetricSet()
    }

    private SourceAnalyzer createSourceAnalyzer() {
        return new AntFileSetSourceAnalyzer(getProject(), fileSets)
    }

}