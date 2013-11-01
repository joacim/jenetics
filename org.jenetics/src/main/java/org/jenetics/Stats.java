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

import java.util.stream.Collector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
public class Stats<G extends Gene<?, G>, C extends Comparable<? super C>> {
	protected Optimize _optimize;
	protected int _generation;
	protected Phenotype<G, C> _best;
	protected Phenotype<G, C> _worst;
	protected int _samples;
	protected double _ageMean;
	protected double _ageVariance;
	protected int _killed;
	protected int _invalid;

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<Phenotype<G, C>, ?, Stats<G, C>> collector() {
		return null;
	}
}
