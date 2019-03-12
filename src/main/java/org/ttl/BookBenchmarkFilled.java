/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.ttl;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import ttl.examples.book.BookAppFilled;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


//@BenchmarkMode(Mode.AverageTime)
//@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Thread)
public class BookBenchmarkFilled {

	@Setup(Level.Trial)
	public void setup() {
		System.out.print("Did setup ");
	}

	@TearDown(Level.Trial)
	public void tearDown() {
		System.out.print("Did shutdown ");
	}

	@Benchmark
	public void testOldWay(Blackhole sink) throws IOException {
		Map<String, Long> result = BookAppFilled.countWordsTheOldWay("PrideAndPrejudice.txt");

		sink.consume(result);
	}

	@Benchmark
	public void testOldWayWithMatcher(Blackhole sink) throws IOException {
		Map<String, Long> result = BookAppFilled.countWordsTheOldWayWithMatcher("PrideAndPrejudice.txt");

		sink.consume(result);
	}

	@Benchmark
	public void testStreamWay(Blackhole sink) throws IOException {
		Map<String, Long> result = BookAppFilled.countWordsStream("PrideAndPrejudice.txt");

		sink.consume(result);
	}

	@Benchmark
	public void testStreamWayWithMatcher(Blackhole sink) throws IOException {
		Map<String, Long> result = BookAppFilled.countWordsStreamMatcher("PrideAndPrejudice.txt");

		sink.consume(result);
	}

	@Benchmark
	public void testStreamWayWithCustomMatcher(Blackhole sink) throws IOException {
		Map<String, Long> result = BookAppFilled.countWordsStreamCustomMatcher("PrideAndPrejudice.txt");

		sink.consume(result);
	}

	@Benchmark
	public void testStreamWayWithMatcherJdk9(Blackhole sink) throws IOException {
		Map<String, Long> result = BookAppFilled.countWordsStreamMatcherJdk9("PrideAndPrejudice.txt");

		sink.consume(result);
	}

	@Benchmark
	public void testParalelStreamWay(Blackhole sink) throws IOException {
		Map<String, Long> result = BookAppFilled.countWordsParallel("PrideAndPrejudice.txt");

		sink.consume(result);
	}

	@Benchmark
	public Map<String, Long> testParalelStreamConcurrentWay(Blackhole sink) throws IOException {
		Map<String, Long> result = BookAppFilled.countWordsParallelConcurrent("PrideAndPrejudice.txt");

		//sink.consume(result);
		return result;
	}

	public static void main(String[] args) throws RunnerException {
		
		Options opt = new OptionsBuilder()
				.include(BookBenchmarkFilled.class.getSimpleName())
				.forks(1)
				//.jvmArgs("-Xms3048m", "-Xmx3048m")
				//.jvmArgs("-XX:+PrintCompilation", "-verbose:gc")
				.build();

		new Runner(opt).run();
	}

}
