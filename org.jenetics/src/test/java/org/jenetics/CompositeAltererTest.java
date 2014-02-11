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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.Array;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-11 $</em>
 */
public class CompositeAltererTest {

	public Alterer<DoubleGene> newAlterer(double p) {
		final double p3 = Math.pow(p, 3);
		return CompositeAlterer.valueOf(
			new Mutator<DoubleGene>(p3),
			new Mutator<DoubleGene>(p3),
			new Mutator<DoubleGene>(p3)
		);
	}

	@Test(dataProvider = "alterCountParameters")
	public void alterCount(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation
	) {
		final Population<DoubleGene, Double> p1 = population(
					ngenes, nchromosomes, npopulation
				);
		final Population<DoubleGene, Double> p2 = p1.copy();
		Assert.assertEquals(p2, p1);

		final Alterer<DoubleGene> mutator = newAlterer(0.01);

		Assert.assertEquals(mutator.alter(p1, 1), diff(p1, p2));
	}

	public static Population<DoubleGene, Double> population(
		final int ngenes,
		final int nchromosomes,
		final int npopulation
	) {
		final Array<DoubleChromosome> chromosomes = new Array<>(nchromosomes);

		for (int i = 0; i < nchromosomes; ++i) {
			chromosomes.set(i, DoubleChromosome.of(0, 10, ngenes));
		}

		final Genotype<DoubleGene> genotype = Genotype.valueOf(chromosomes.toISeq());
		final Population<DoubleGene, Double> population = new Population<>(npopulation);

		for (int i = 0; i < npopulation; ++i) {
			population.add(Phenotype.valueOf(genotype.newInstance(), TestUtils.FF, 0));
		}

		return population;
	}

	/*
	 * Count the number of different genes.
	 */
	public int diff(
		final Population<DoubleGene, Double> p1,
		final Population<DoubleGene, Double> p2
	) {
		int count = 0;
		for (int i = 0; i < p1.size(); ++i) {
			final Genotype<?> gt1 = p1.get(i).getGenotype();
			final Genotype<?> gt2 = p2.get(i).getGenotype();

			for (int j = 0; j < gt1.length(); ++j) {
				final Chromosome<?> c1 = gt1.getChromosome(j);
				final Chromosome<?> c2 = gt2.getChromosome(j);

				for (int k = 0; k < c1.length(); ++k) {
					if (!c1.getGene(k).equals(c2.getGene(k))) {
						++count;
					}
				}
			}
		}
		return count;
	}

	@DataProvider(name = "alterCountParameters")
	public Object[][] alterCountParameters() {
		return new Object[][] {
				//    ngenes,       nchromosomes     npopulation
				{ new Integer(1),   new Integer(1),  new Integer(100) },
				{ new Integer(5),   new Integer(1),  new Integer(100) },
				{ new Integer(80),  new Integer(1),  new Integer(100) },
				{ new Integer(1),   new Integer(2),  new Integer(100) },
				{ new Integer(5),   new Integer(2),  new Integer(100) },
				{ new Integer(80),  new Integer(2),  new Integer(100) },
				{ new Integer(1),   new Integer(15), new Integer(100) },
				{ new Integer(5),   new Integer(15), new Integer(100) },
				{ new Integer(80),  new Integer(15), new Integer(100) },

				{ new Integer(1),   new Integer(1),  new Integer(150) },
				{ new Integer(5),   new Integer(1),  new Integer(150) },
				{ new Integer(80),  new Integer(1),  new Integer(150) },
				{ new Integer(1),   new Integer(2),  new Integer(150) },
				{ new Integer(5),   new Integer(2),  new Integer(150) },
				{ new Integer(80),  new Integer(2),  new Integer(150) },
				{ new Integer(1),   new Integer(15), new Integer(150) },
				{ new Integer(5),   new Integer(15), new Integer(150) },
				{ new Integer(80),  new Integer(15), new Integer(150) },

				{ new Integer(1),   new Integer(1),  new Integer(500) },
				{ new Integer(5),   new Integer(1),  new Integer(500) },
				{ new Integer(80),  new Integer(1),  new Integer(500) },
				{ new Integer(1),   new Integer(2),  new Integer(500) },
				{ new Integer(5),   new Integer(2),  new Integer(500) },
				{ new Integer(80),  new Integer(2),  new Integer(500) },
				{ new Integer(1),   new Integer(15), new Integer(500) },
				{ new Integer(5),   new Integer(15), new Integer(500) },
				{ new Integer(80),  new Integer(15), new Integer(500) }
		};
	}
	@Test
	public void join() {
		CompositeAlterer<DoubleGene> alterer = CompositeAlterer.join(
				new Mutator<DoubleGene>(),
				new NormalMutator<Double, DoubleGene>()
			);

		Assert.assertEquals(alterer.getAlterers().length(), 2);
		Assert.assertEquals(alterer.getAlterers().get(0), new Mutator<DoubleGene>());
		Assert.assertEquals(alterer.getAlterers().get(1), new NormalMutator<Double, DoubleGene>());

		alterer = CompositeAlterer.join(alterer, new MeanAlterer<DoubleGene>());

		Assert.assertEquals(alterer.getAlterers().length(), 3);
		Assert.assertEquals(alterer.getAlterers().get(0), new Mutator<DoubleGene>());
		Assert.assertEquals(alterer.getAlterers().get(1), new NormalMutator<Double, DoubleGene>());
		Assert.assertEquals(alterer.getAlterers().get(2), new MeanAlterer<DoubleGene>());

		alterer = CompositeAlterer.valueOf(
			new MeanAlterer<DoubleGene>(),
			new SwapMutator<DoubleGene>(),
			alterer,
			new SwapMutator<DoubleGene>()
		);

		Assert.assertEquals(alterer.getAlterers().length(), 6);
		Assert.assertEquals(alterer.getAlterers().get(0), new MeanAlterer<DoubleGene>());
		Assert.assertEquals(alterer.getAlterers().get(1), new SwapMutator<DoubleGene>());
		Assert.assertEquals(alterer.getAlterers().get(2), new Mutator<DoubleGene>());
		Assert.assertEquals(alterer.getAlterers().get(3), new NormalMutator<Double, DoubleGene>());
		Assert.assertEquals(alterer.getAlterers().get(4), new MeanAlterer<DoubleGene>());
		Assert.assertEquals(alterer.getAlterers().get(5), new SwapMutator<DoubleGene>());
	}

}
