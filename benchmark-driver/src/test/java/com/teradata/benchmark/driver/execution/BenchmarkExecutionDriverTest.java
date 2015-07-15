/*
 * Copyright 2013-2015, Teradata, Inc. All rights reserved.
 */

package com.teradata.benchmark.driver.execution;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.teradata.benchmark.driver.Benchmark;
import com.teradata.benchmark.driver.concurrent.ExecutorServiceFactory;
import com.teradata.benchmark.driver.listeners.benchmark.BenchmarkStatusReporter;
import com.teradata.benchmark.driver.macro.MacroService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BenchmarkExecutionDriverTest
{

    @Mock
    MacroService macroService;

    @Mock
    ExecutorServiceFactory executorServiceFactory;

    @Mock
    ListeningExecutorService executorService;

    @Mock
    BenchmarkStatusReporter statusReporter;

    @Mock
    ExecutionSynchronizer executionSynchronizer;

    @InjectMocks
    BenchmarkExecutionDriver driver;

    @Before
    public void setUp()
    {
        when(executorServiceFactory.create(anyInt())).thenReturn(executorService);
    }

    @Test
    public void successfulRun()
    {

        BenchmarkExecutionResult benchmarkExecutionResult = driver.execute(mock(Benchmark.class), 0, 0);

        benchmarkExecutionResult.getFailureCauses().stream().forEach(Exception::printStackTrace);
        assertThat(benchmarkExecutionResult.getFailureCauses()).isEmpty();
        assertThat(benchmarkExecutionResult.isSuccessful()).isTrue();
    }

    @Test
    public void afterMacroFailureCausesBenchmarkExecutionToFail()
    {
        RuntimeException afterMacroException = new RuntimeException();
        doNothing().doThrow(afterMacroException)
                .when(macroService).runBenchmarkMacros(anyList());

        BenchmarkExecutionResult benchmarkExecutionResult = driver.execute(mock(Benchmark.class), 0, 0);

        assertThat(benchmarkExecutionResult.isSuccessful()).isFalse();
        assertThat(benchmarkExecutionResult.getFailureCauses()).containsExactly(afterMacroException);
    }

    @Test
    public void afterMacroFailureCausesDoNotOverrideBenchmarkExecutionFailure()
    {
        IllegalStateException afterMacroException = new IllegalStateException();
        IllegalArgumentException executorServiceException = new IllegalArgumentException();
        doThrow(executorServiceException)
                .when(executorServiceFactory).create(anyInt());
        doNothing().doThrow(afterMacroException)
                .when(macroService).runBenchmarkMacros(anyList());
        
        BenchmarkExecutionResult benchmarkExecutionResult = driver.execute(mock(Benchmark.class), 0, 0);

        assertThat(benchmarkExecutionResult.isSuccessful()).isFalse();
        assertThat(benchmarkExecutionResult.getFailureCauses()).containsExactly(executorServiceException);
    }
}
