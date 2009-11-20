/*
 * Copyright 2008 the original author or authors.
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
package org.gmetrics.analyzer

import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metricset.ListMetricSet
import org.gmetrics.result.StubMetricResult
import org.gmetrics.resultsnode.ResultsNodeTestUtil
import org.gmetrics.metric.linecount.MethodLineCountMetric
import org.gmetrics.result.StubMetricResult

/**
 * Integration tests for FilesystemSourceAnalyzer. These tests access the real filesystem
 * and use a real Metric implementation.
 *
 * @author Chris Mair
 * @version $Revision: 180 $ - $Date: 2009-07-11 18:30:19 -0400 (Sat, 11 Jul 2009) $
 */
class FilesystemSourceAnalyzer_IntegrationTest extends AbstractTestCase {

    private static final BASE_DIR = 'src/test/resources/source'
    private analyzer
    private metric
    private metricSet

    void setUp() {
        super.setUp()
        analyzer = new FilesystemSourceAnalyzer(baseDirectory:BASE_DIR)
        metric = new MethodLineCountMetric()
        metricSet = new ListMetricSet([metric])
    }

    void testAnalyze_EmptyDirectory() {
        analyzer.baseDirectory = BASE_DIR + '/empty'
        def results = new StubMetricResult(metric:metric, count:0, totalValue:0, averageValue:0)
        assertAnalyze_ResultsNodeStructure([metricResults:[results]])
    }

    void testAnalyze_NoMatchingFiles() {
        analyzer.baseDirectory = BASE_DIR + '/no_matching_files'
        def results = new StubMetricResult(metric:metric, count:0, totalValue:0, averageValue:0)
        assertAnalyze_ResultsNodeStructure([metricResults:[results]])
    }

    void testAnalyze() {
        def classA1_method1 = new StubMetricResult(metric:metric, count:1, totalValue:3, averageValue:3)
        def classA1_method2 = new StubMetricResult(metric:metric, count:1, totalValue:5, averageValue:5)
        def classA1 = new StubMetricResult(metric:metric, count:2, totalValue:8, averageValue:4)
        def classA2_method1 = new StubMetricResult(metric:metric, count:1, totalValue:4, averageValue:4)
        def classA2_method2 = new StubMetricResult(metric:metric, count:1, totalValue:4, averageValue:4)
        def classA2 = new StubMetricResult(metric:metric, count:2, totalValue:8, averageValue:4)
        def dirA = new StubMetricResult(metric:metric, count:4, totalValue:16, averageValue:4)

        def classB1_method1 = new StubMetricResult(metric:metric, count:1, totalValue:1, averageValue:1)
        def classB1_method2 = new StubMetricResult(metric:metric, count:1, totalValue:1, averageValue:1)
        def classB1_closure3 = new StubMetricResult(metric:metric, count:1, totalValue:7, averageValue:7)
        def classB1 = new StubMetricResult(metric:metric, count:3, totalValue:9, averageValue:3)
        def dirB = new StubMetricResult(metric:metric, count:3, totalValue:9, averageValue:3)

        def all = new StubMetricResult(metric:metric, count:7, totalValue:25, averageValue:scale(25/7))

        assertAnalyze_ResultsNodeStructure([
            metricResults:[all],
            children:[
                dirA:[
                    metricResults:[dirA],
                    children:[
                        'ClassA1':[
                            metricResults:[classA1],
                            children:[
                                'method1':[metricResults:[classA1_method1]],
                                'method2':[metricResults:[classA1_method2]]
                            ]
                        ],
                        'ClassA2':[
                            metricResults:[classA2],
                            children:[
                                'method1':[metricResults:[classA2_method1]],
                                'method2':[metricResults:[classA2_method2]]
                            ]
                        ]
                    ]
                ],

                dirB:[
                    metricResults:[dirB],
                    children:[
                        'ClassB1':[
                            metricResults:[classB1],
                            children:[
                                'method1':[metricResults:[classB1_method1]],
                                'method2':[metricResults:[classB1_method2]],
                                'closure3':[metricResults:[classB1_closure3]]
                            ]
                        ]
                    ]
                ]

        ]])
    }

    // --------------------------- Internal helper methods ---------------------------------------

    private void assertAnalyze_ResultsNodeStructure(Map resultsNodeStructure) {
        def resultsNode = analyzer.analyze(metricSet)
        log("resultsNode=$resultsNode")
        ResultsNodeTestUtil.assertResultsNodeStructure(resultsNode, resultsNodeStructure)
    }
}