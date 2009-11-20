/*
 * Copyright 2009 the original author or authors.
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
package org.gmetrics.resultsnode

import org.gmetrics.metric.MetricLevel
import org.gmetrics.result.MetricResult
import org.gmetrics.metric.Metric
import org.gmetrics.result.ClassMetricResult
import org.gmetrics.result.MetricResult

/**
 * Represents a node in the hierarchy of metric result nodes
 *
 * @author Chris Mair
 * @version $Revision: 228 $ - $Date: 2009-09-29 21:52:31 -0400 (Tue, 29 Sep 2009) $
 */
class ClassResultsNode implements ResultsNode {

    final MetricLevel level = MetricLevel.CLASS
    final Map children = [:]
    final List metricResults = []

    boolean containsClassResults() {
        return true
    }

    MetricResult getMetricResult(Metric metric) {
        assert metric
        return metricResults.find { metricResult -> metricResult.metric == metric }
    }

    void addClassMetricResult(ClassMetricResult classMetricResult) {
        assert classMetricResult
        metricResults << classMetricResult.classMetricResult

        def methodMetricResults = classMetricResult.getMethodMetricResults()
        methodMetricResults.each { k, v ->
            addMethodMetricResult(k, v)
        }
    }

    String toString() {
        return "ClassResultsNode[$level: metricResults=$metricResults, children=$children]"
    }

    private void addMethodMetricResult(String methodName, MetricResult metricResult) {
        if (children[methodName] == null) {
            children[methodName] = new MethodResultsNode()
        }
        children[methodName].addMetricResult(metricResult)
    }

}