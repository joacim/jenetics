/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics;

import static org.jenetics.stat.StatisticsAssert.assertUniformDistribution;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.Variance;
import org.jenetics.util.Accumulator.MinMax;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class DoubleChromosomeTest
	extends NumericChromosomeTester<Double, DoubleGene>
{

	private final DoubleChromosome _factory = new DoubleChromosome(
		0.0, Double.MAX_VALUE, 500
	);

	@Override
	protected DoubleChromosome factory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		try (Scoped<?> s = RandomRegistry.scope(new Random(12345))) {

			final double min = 0;
			final double max = 100;


			final MinMax<Double> mm = new MinMax<>();
			final Histogram<Double> histogram = Histogram.of(min, max, 10);
			final Variance<Double> variance = new Variance<>();

			for (int i = 0; i < 1000; ++i) {
				final DoubleChromosome chromosome = new DoubleChromosome(min, max, 500);
				for (DoubleGene gene : chromosome) {
					mm.accumulate(gene.getAllele());
					histogram.accept(gene.getAllele());
					variance.accumulate(gene.getAllele());
				}
			}

			Assert.assertTrue(mm.getMin().compareTo(0.0) >= 0);
			Assert.assertTrue(mm.getMax().compareTo(100.0) <= 100);
			assertUniformDistribution(histogram);
		}
	}

}
