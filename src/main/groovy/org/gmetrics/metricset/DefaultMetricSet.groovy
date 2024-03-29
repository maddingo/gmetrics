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
package org.gmetrics.metricset

import org.gmetrics.metric.linecount.MethodLineCountMetric
import org.gmetrics.metric.linecount.ClassLineCountMetric
import org.gmetrics.metric.cyclomatic.CyclomaticComplexityMetric

/**
 * A <code>MetricSet</code> implementation that returns the default static List of Metrics.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class DefaultMetricSet implements MetricSet {

    private metricSet = new ListMetricSet([new CyclomaticComplexityMetric(), new ClassLineCountMetric(), new MethodLineCountMetric()])

    List getMetrics() {
        return metricSet.getMetrics()
    }
}