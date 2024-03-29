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
package org.gmetrics.metric.abc.result

import org.gmetrics.metric.Metric
import org.gmetrics.test.AbstractTestCase
import org.gmetrics.metric.abc.AbcTestUtil
import org.gmetrics.metric.abc.AbcVector
import org.gmetrics.metric.MetricLevel

/**
 * Tests for AggregateAbcMetricResult
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AggregateAbcMetricResultTest extends AbstractTestCase {

    private static final DEFAULT_FUNCTIONS = ['total', 'average', 'minimum', 'maximum']
    private static final METRIC = [getFunctions:{ DEFAULT_FUNCTIONS }] as Metric
    private aggregateAbcMetricResult

    void testConstructorThrowsExceptionForNullMetricParameter() {
        shouldFailWithMessageContaining('metric') { new AggregateAbcMetricResult(null, MetricLevel.METHOD, []) }
    }

    void testConstructorThrowsExceptionForNullMetricLevelParameter() {
        shouldFailWithMessageContaining('metric') { new AggregateAbcMetricResult(METRIC, null, []) }
    }

    void testConstructorThrowsExceptionForNullChildrenParameter() {
        shouldFailWithMessageContaining('children') { new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, null) }
    }

    void testConstructorSetsMetricProperly() {
        def mr = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, [])
        assert mr.metric == METRIC
    }

    void testConstructorSetsMetricLevelProperly() {
        def mr = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, [])
        assert mr.metricLevel == MetricLevel.METHOD
    }

    void testGetLineNumberIsSameValuePassedIntoConstructor() {
        def result = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, [], 67)
        assert result.getLineNumber() == 67
    }

    // Tests for no children

    void testAverageAbcVectorForNoVectorsIsZeroVector() {
        initializeWithZeroChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.averageAbcVector, [0, 0, 0])
    }

    void testTotalAbcVectorForNoVectorsIsZeroVector() {
        initializeWithZeroChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.totalAbcVector, [0, 0, 0])
    }

//    void testMinimumAbcVectorForNoVectorsIsZeroVector() {
//        initializeWithZeroChildMetricResults()
//        AbcTestUtil.assertEquals(aggregateAbcMetricResult.minimumAbcVector, [0, 0, 0])
//    }
//
//    void testMaximumAbcVectorForNoVectorsIsZeroVector() {
//        initializeWithZeroChildMetricResults()
//        AbcTestUtil.assertEquals(aggregateAbcMetricResult.maximumAbcVector, [0, 0, 0])
//    }

    void testAverageValueForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult['average'] == 0
    }

    void testTotalValueForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult['total'] == 0
    }

    void testMinimumValueForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult['minimum'] == 0
    }

    void testMaximumValueForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult['maximum'] == 0
    }

    void testCountForNoVectorsIsZero() {
        initializeWithZeroChildMetricResults()
        assert aggregateAbcMetricResult.count == 0
    }

    // Tests for single child

    void testAverageAbcVectorForSingleVectorIsThatVector() {
        initializeWithOneChildMetricResult()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.averageAbcVector, [7, 9, 21])
    }

    void testTotalAbcVectorForSingleVectorIsThatVector() {
        initializeWithOneChildMetricResult()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.totalAbcVector, [7, 9, 21])
    }

    void testMinimumValueForSingleVectorsIsThatVectorMagnitude() {
        initializeWithOneChildMetricResult()
        assert aggregateAbcMetricResult['minimum'] == new AbcVector(7, 9, 21).magnitude
    }

    void testMaximumValueForSingleVectorsIsThatVectorMagnitude() {
        initializeWithOneChildMetricResult()
        assert aggregateAbcMetricResult['maximum'] == new AbcVector(7, 9, 21).magnitude
    }

    // Tests for several children

    void testCorrectRoundedAverageForSeveralVectors() {
        initializeWithThreeChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.averageAbcVector, [9, 4, 23])     // A is rounded down; C is rounded up
    }

    void testCorrectTotalAbcVectorForSeveralVectors() {
        initializeWithThreeChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.totalAbcVector, [27, 12, 68])
    }

    void testAbcVectorIsTheSameAsTheTotalAbcVector() {
        initializeWithThreeChildMetricResults()
        AbcTestUtil.assertEquals(aggregateAbcMetricResult.abcVector, [27, 12, 68])
    }

    void testTotalValueForSeveralVectorsIsTheMagnitudeOfTheSumOfTheVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult['total'] == new AbcVector(27, 12, 68).magnitude
    }

    void testAverageValueForSeveralVectorsIsTheMagnitudeOfTheAverageOfTheVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult['average'] == new AbcVector(9, 4, 23).magnitude
    }

    void testMinimumValueForSeveralVectorsIsTheMinimumMagnitudeOfTheVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult['minimum'] == new AbcVector(7, 9, 21).magnitude
    }

    void testMaximumValueForSeveralVectorsIsTheMaximumMagnitudeOfTheVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult['maximum'] == new AbcVector(9, 2, 25).magnitude
    }

    void testCorrectCountForSeveralVectors() {
        initializeWithThreeChildMetricResults()
        assert aggregateAbcMetricResult.count == 3
    }

    void testCorrectCountForChildResultsWithCountsGreaterThanOne() {
        initializeWithThreeChildMetricResults()
        def aggregate = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, [aggregateAbcMetricResult, aggregateAbcMetricResult])
        assert aggregate.count == 6
    }

    // Other tests

    void testGetValueForUnknownFunctionIsNull() {
        initializeWithOneChildMetricResult()
        assert aggregateAbcMetricResult['xxx'] == null
    }

    void testUsesFunctionNamesFromMetric() {
        final FUNCTION_NAMES = ['average', 'maximum']
        def metric = [getName:{'TestMetric'}, getFunctions:{ FUNCTION_NAMES }] as Metric
        aggregateAbcMetricResult = new AggregateAbcMetricResult(metric, MetricLevel.METHOD, [])
        assert aggregateAbcMetricResult['average'] != null
        assert aggregateAbcMetricResult['maximum'] != null
        assert aggregateAbcMetricResult['total'] == null
        assert aggregateAbcMetricResult['minimum'] == null
    }

    //--------------------------------------------------------------------------
    // Helper Methods
    //--------------------------------------------------------------------------
    
    private void initializeWithZeroChildMetricResults() {
        aggregateAbcMetricResult = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, [])
    }

    private void initializeWithOneChildMetricResult() {
        def children = [AbcTestUtil.abcMetricResult(METRIC, 7, 9, 21)]
        aggregateAbcMetricResult = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, children)
    }

    private void initializeWithThreeChildMetricResults() {
        def child1 = AbcTestUtil.abcMetricResult(METRIC, 7, 9, 21)
        def child2 = AbcTestUtil.abcMetricResult(METRIC, 11, 1, 22)
        def child3 = AbcTestUtil.abcMetricResult(METRIC, 9, 2, 25)
        aggregateAbcMetricResult = new AggregateAbcMetricResult(METRIC, MetricLevel.METHOD, [child1, child2, child3])
    }
}