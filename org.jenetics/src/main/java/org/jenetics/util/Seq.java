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
package org.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jenetics.internal.util.SeqIteratorAdapter;
import org.jenetics.internal.util.SeqListAdapter;
import org.jenetics.internal.util.SeqMappedIteratorAdapter;

/**
 * General interface for a ordered, fixed sized, object sequence.
 * <br/>
 * Use the {@link #asList()} method to work together with the
 * <a href="http://download.oracle.com/javase/6/docs/technotes/guides/collections/index.html">
 * Java Collection Framework</a>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version @__new_version__@ &mdash; <em>$Date$</em>
 */
public interface Seq<T> extends Iterable<T> {

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index index of the element to return.
	 * @return the value at the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *          {@code (index < 0 || index >= size())}.
	 */
	public T get(final int index);

	/**
	 * Return the length of this sequence. Once the sequence is created, the
	 * length can't be changed.
	 *
	 * @return the length of this sequence.
	 */
	public int length();

	/**
	 * @see #length()
	 */
	public default int size() {
		return length();
	}

	/**
	 * Return default implementation of the {@code Iterable} method. Only uses
	 * the {@link #get(int)} and {@link #length()} method for implementing the
	 * Iterator. Implementing classes encouraged to override this method if
	 * needed.
	 *
	 * @return an iterator over the elements of the sequence.
	 */
	@Override
	public default Iterator<T> iterator() {
		return new SeqIteratorAdapter<T>(this);
	}

	/**
	 * Return an iterator with the new type {@code B}.
	 *
	 * @param <B> the component type of the returned type.
	 * @param mapper the converter for converting from {@code T} to {@code B}.
	 * @return the iterator of the converted type.
	 * @throws NullPointerException if the given {@code converter} is
	 *        {@code null}.
	 */
	public default <B> Iterator<B> iterator(
		final Function<? super T, ? extends B> mapper
	) {
		return new SeqMappedIteratorAdapter<>(this, mapper);
	}

	/**
	 * Tests whether a predicate holds for all elements of this sequence.
	 *
	 * @param predicate the predicate to use to test the elements.
	 * @return {@code true} if the given predicate p holds for all elements of
	 *          this sequence, {@code false} otherwise.
	 * @throws NullPointerException if the given {@code predicate} is
	 *          {@code null}.
	 */
	public default boolean forAll(final Predicate<? super T> predicate) {
		boolean valid = true;

		if (this instanceof RandomAccess) {
			for (int i = 0, n = length(); i < n && valid; ++i) {
				valid = predicate.test(get(i));
			}
			return valid;
		} else {
			final Iterator<T> it = iterator();
			while (it.hasNext() && valid) {
				valid = predicate.test(it.next());
			}
		}

		return valid;
	}

	public default <B> B foldLeft(
		final B z,
		final BiFunction<? super B, ? super T, ? extends B> op
	) {
		B result = z;
		for (int i = 0, n = length(); i < n; ++i) {
			result = op.apply(result, get(i));
		}
		return result;
	}

	public default <B> B foldRight(
		final B z,
		final BiFunction<? super T, ? super B, ? extends B> op
	) {
		B result = z;
		for (int i = length(); --i >= 0;) {
			result = op.apply(get(i), result);
		}
		return result;
	}

	/**
	 * Returns {@code true} if this sequence contains the specified element.
	 *
	 * @param element element whose presence in this sequence is to be tested.
	 *        The tested element can be {@code null}.
	 * @return {@code true} if this sequence contains the specified element
	 */
	public default boolean contains(final Object element) {
		return indexOf(element) != -1;
	}

	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @return the index of the first occurrence of the specified element in
	 *          this sequence, or -1 if this sequence does not contain the element
	 */
	public default int indexOf(final Object element) {
		return indexOf(element, 0, length());
	}

	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @param start the start index (inclusively) for the element search.
	 * @return the index of the first occurrence of the specified element in
	 *          this sequence, or -1 if this sequence does not contain the element
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || start > length()}).
	 */
	public default int indexOf(final Object element, final int start) {
		return indexOf(element, start, length());
	}

	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @param start the start index (inclusively) for the element search.
	 * @param end the end index (exclusively) for the element search.
	 * @return the index of the first occurrence of the specified element in
	 *          this sequence, or -1 if this sequence does not contain the element
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	public default int indexOf(final Object element, final int start, final int end) {
		int index = -1;
		if (element != null) {
			index = indexWhere(o -> element.equals(o), start, end);
		} else {
			index = indexWhere(o -> o == null, start, end);
		}
		return index;
	}

	/**
	 * <p>
	 * Returns the index of the first element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 * </p>
	 * [code]
	 * // Finding index of first null value.
	 * final int index = seq.indexOf(new Predicates.Nil());
	 *
	 * // Assert of no null values.
	 * assert (sequence.indexOf(new Predicates.Nil()) == -1);
	 * [/code]
	 *
	 * @param predicate the search predicate.
	 * @return the index of the first element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns {@code false}
	 *          for every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 */
	public default int indexWhere(final Predicate<? super T> predicate) {
		return indexWhere(predicate, 0, length());
	}

	/**
	 * <p>
	 * Returns the index of the first element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 * </p>
	 * [code]
	 * // Finding index of first null value.
	 * final int index = seq.indexOf(new Predicates.Nil());
	 *
	 * // Assert of no null values.
	 * assert (sequence.indexOf(new Predicates.Nil()) == -1);
	 * [/code]
	 *
	 * @param predicate the search predicate.
	 * @return the index of the first element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns {@code false}
	 *          for every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || start > length()}).
	 */
	public default int indexWhere(
		final Predicate<? super T> predicate,
		final int start
	) {
		return indexWhere(predicate, start, length());
	}

	/**
	 * <p>
	 * Returns the index of the first element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 * </p>
	 * [code]
	 * // Finding index of first null value.
	 * final int index = seq.indexOf(new Predicates.Nil());
	 *
	 * // Assert of no null values.
	 * assert (sequence.indexOf(new Predicates.Nil()) == -1);
	 * [/code]
	 *
	 * @param predicate the search predicate.
	 * @return the index of the first element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns {@code false}
	 *          for every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	public default int indexWhere(
		final Predicate<? super T> predicate,
		final int start,
		final int end
	) {
		requireNonNull(predicate, "Predicate");

		int index = -1;
		for (int i = 0, n = length(); i < n && index == -1; ++i) {
			if (predicate.test(get(i))) {
				index = i;
			}
		}
		return index;
	}

	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @return the index of the last occurrence of the specified element in
	 * 		  this sequence, or -1 if this sequence does not contain the element
	 */
	public default int lastIndexOf(final Object element) {
		return lastIndexOf(element, 0, length());
	}

	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @return the index of the last occurrence of the specified element in
	 * 		  this sequence, or -1 if this sequence does not contain the element
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code end < 0 || end > length()}).
	 */
	public default int lastIndexOf(final Object element, final int end) {
		return lastIndexOf(element, 0, end);
	}

	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @return the index of the last occurrence of the specified element in
	 * 		  this sequence, or -1 if this sequence does not contain the element
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	public default int lastIndexOf(
		final Object element,
		final int start,
		final int end
	) {
		int index = -1;

		if (element == null) {
			index = lastIndexWhere(o -> o == null, start, end);
		} else {
			index = lastIndexWhere(o -> element.equals(o), start, end);
		}

		return index;
	}

	/**
	 * Returns the index of the last element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 *
	 * @param predicate the search predicate.
	 * @return the index of the last element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns false for
	 *          every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 */
	public default int lastIndexWhere(final Predicate<? super T> predicate) {
		return lastIndexWhere(predicate, 0, length());
	}

	/**
	 * Returns the index of the last element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 *
	 * @param predicate the search predicate.
	 * @return the index of the last element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns false for
	 *          every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code end < 0 || end > length()}).
	 */
	public default int lastIndexWhere(
		final Predicate<? super T> predicate,
		final int end
	) {
		return lastIndexWhere(predicate, 0, end);
	}

	/**
	 * Returns the index of the last element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 *
	 * @param predicate the search predicate.
	 * @return the index of the last element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns false for
	 *          every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	public default int lastIndexWhere(
		final Predicate<? super T> predicate,
		final int start,
		final int end
	) {
		requireNonNull(predicate, "Predicate");

		int index = -1;
		for (int i = length(); --i >= 0 && index == -1;) {
			if (predicate.test(get(i))) {
				index = i;
			}
		}
		return index;
	}

	/**
	 * Returns a fixed-size list backed by the specified sequence. (Changes to
	 * the returned list "write through" to the array.) The returned list is
	 * fixed size, serializable and implements {@link RandomAccess}.
	 *
	 * @return a list view of this sequence
	 */
	public default List<T> asList() {
		return new SeqListAdapter<T>(this);
	}

	/**
	 * Builds a new sequence by applying a function to all elements of this
	 * sequence.
	 *
	 * @param <B> the element type of the returned collection.
	 * @param mapper the function to apply to each element.
	 * @return a new sequence of type That resulting from applying the given
	 *         function f to each element of this sequence and collecting the
	 *         results.
	 * @throws NullPointerException if the element {@code mapper} is
	 *         {@code null}.
	 */
	public <B> Seq<B> map(final Function<? super T, ? extends B> mapper);

	/**
	 * Return an array containing all of the elements in this sequence in right
	 * order. The returned array will be "safe" in that no references to it
	 * are maintained by this sequence. (In other words, this method must allocate
	 * a new array.) The caller is thus free to modify the returned array.
	 *
	 * @see java.util.Collection#toArray()
	 *
	 * @return an array containing all of the elements in this list in right
	 *          order
	 */
	public default Object[] toArray() {
		final Object[] array = new Object[size()];
		for (int i = size(); --i >= 0;) {
			array[i] = get(i);
		}
		return array;
	}

	/**
	 * Return an array containing all of the elements in this sequence in right
	 * order; the runtime type of the returned array is that of the specified
	 * array. If this sequence fits in the specified array, it is returned therein.
	 * Otherwise, a new array is allocated with the runtime type of the specified
	 * array and the length of this array.
	 * <p/>
	 * If this sequence fits in the specified array with room to spare (i.e., the
	 * array has more elements than this array), the element in the array
	 * immediately following the end of this array is set to null. (This is
	 * useful in determining the length of the array only if the caller knows
	 * that the list does not contain any null elements.)
	 *
	 * @see java.util.Collection#toArray(Object[])
	 *
	 * @param array the array into which the elements of this array are to be
	 *         stored, if it is big enough; otherwise, a new array of the same
	 *         runtime type is allocated for this purpose.
	 * @return an array containing the elements of this array
	 * @throws ArrayStoreException if the runtime type of the specified array is
	 *          not a super type of the runtime type of every element in this array
	 * @throws NullPointerException if the given array is {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public default T[] toArray(final T[] array) {
		if (array.length < length()) {
			final T[] copy = (T[])java.lang.reflect.Array.newInstance(
				array.getClass().getComponentType(), length()
			);
			for (int i = length(); --i >= 0;) {
				copy[i] = get(i);
			}

			return copy;
		}

		for (int i = 0, n = length(); i < n; ++i) {
			array[i] = get(i);
		}
		return array;
	}

	/**
	 * Returns a view of the portion of this sequence between the specified
	 * {@code start}, inclusive, and {@code end}, exclusive. (If {@code start}
	 * and {@code end} are equal, the returned sequence has the length zero.) The
	 * returned sequence is backed by this sequence, so non-structural changes
	 * in the returned sequence are reflected in this sequence, and vice-versa.
	 * <p/>
	 * This method eliminates the need for explicit range operations (of the
	 * populationSort that commonly exist for arrays). Any operation that expects an sequence
	 * can be used as a range operation by passing an sub sequence view instead of
	 * an whole sequence.
	 *
	 * @param start low end point (inclusive) of the sub array.
	 * @return a view of the specified range within this array.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || start > length()}).
	 */
	public Seq<T> subSeq(final int start);

	/**
	 * Returns a view of the portion of this sequence between the specified
	 * {@code start}, inclusive, and {@code end}, exclusive. (If {@code start}
	 * and {@code end} are equal, the returned sequence has the length zero.) The
	 * returned sequence is backed by this sequence, so non-structural changes in the
	 * returned sequence are reflected in this array, and vice-versa.
	 * <p/>
	 * This method eliminates the need for explicit range operations (of the
	 * populationSort that commonly exist for arrays). Any operation that expects an array
	 * can be used as a range operation by passing an sub sequence view instead of
	 * an whole sequence.
	 *
	 * @param start low end point (inclusive) of the sub sequence.
	 * @param end high end point (exclusive) of the sub sequence.
	 * @return a view of the specified range within this sequence.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	public Seq<T> subSeq(final int start, final int end);

	/**
	 * Test whether the given array is sorted in ascending order.
	 *
	 * @return {@code true} if the given {@code array} is sorted in ascending
	 *         order, {@code false} otherwise.
	 * @throws NullPointerException if the given array or one of it's element is
	 *         {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public default boolean isSorted() {
		boolean sorted = true;
		for (int i = 0, n = length() - 1; i < n && sorted; ++i) {
			sorted = ((Comparable<T>)get(i)).compareTo(get(i + 1)) <= 0;
		}

		return sorted;
	}

	/**
	 * Test whether the given array is sorted in ascending order. The order of
	 * the array elements is defined by the given comparator.
	 *
	 * @param comparator the comparator which defines the order.
	 * @return {@code true} if the given {@code array} is sorted in ascending
	 *         order, {@code false} otherwise.
	 * @throws NullPointerException if the given array or one of it's element or
	 *         the comparator is {@code null}.
	 */
	public default boolean isSorted(final Comparator<? super T> comparator) {
		boolean sorted = true;
		for (int i = 0, n = length() - 1; i < n && sorted; ++i) {
			sorted = comparator.compare(get(i), get(i + 1)) <= 0;
		}

		return sorted;
	}

	/**
	 * Returns the hash code value for this sequence. The hash code is defined
	 * as followed:
	 *
	 * [code]
	 * int hashCode = 1;
	 * final Iterator<E> it = seq.iterator();
	 * while (it.hasNext()) {
	 *     final E obj = it.next();
	 *     hashCode = 31*hashCode + (obj == null ? 0 : obj.hashCode());
	 * }
	 * [/code]
	 *
	 * @see List#hashCode()
	 *
	 * @return the hash code value for this list
	 */
	@Override
	public int hashCode();
	/*
	{
		int hash = 1;
		for (Object element : this) {
			hash = 31*hash + (element == null ? 0: element.hashCode());
		}
		return hash;
	}
	*/

	/**
	 * Compares the specified object with this sequence for equality. Returns
	 * true if and only if the specified object is also a sequence, both
	 * sequence have the same size, and all corresponding pairs of elements in
	 * the two sequences are equal. (Two elements e1 and e2 are equal if
	 * (e1==null ? e2==null : e1.equals(e2)).) This definition ensures that the
	 * equals method works properly across different implementations of the Seq
	 * interface.
	 *
	 * @see List#equals(Object)
	 *
	 * @param object the object to be compared for equality with this sequence.
	 * @return {@code true} if the specified object is equal to this sequence,
	 *          {@code false} otherwise.
	 */
	@Override
	public boolean equals(final Object object);
	/*
	{
		if (object == this) {
			return true;
		}
		if (!(object instanceof Seq<?>)) {
			return false;
		}

		final Seq<?> other = (Seq<?>)object;
		boolean equals = (length() == other.length());
		for (int i = length(); equals && --i >= 0;) {
			final Object element = get(i);
			if (element != null) {
				equals = element.equals(other.get(i));
			} else {
				equals = other.get(i) == null;
			}
		}
		return equals;
	}
	*/

	/**
	 * Create a string representation of the given sequence.
	 *
	 * @param prefix the prefix of the string representation; e.g {@code '['}.
	 * @param separator the separator of the array elements; e.g. {@code ','}.
	 * @param suffix the suffix of the string representation; e.g. {@code ']'}.
	 * @return the string representation of this sequence.
	 */
	public default String toString(
		final String prefix,
		final String separator,
		final String suffix
	) {
		final StringBuilder out = new StringBuilder();

		out.append(prefix);
		if (length() > 0) {
			out.append(get(0));
		}
		for (int i =  1; i < length(); ++i) {
			out.append(separator);
			out.append(get(i));
		}
		out.append(suffix);

		return out.toString();
	}

	/**
	 * Create a string representation of the given sequence.
	 *
	 * @param separator the separator of the array elements; e.g. {@code ','}.
	 * @return the string representation of this sequence.
	 */
	public default String toString(final String separator) {
		return toString("", separator, "");
	}

	/**
	 * Unified method for calculating the hash code of every {@link Seq}
	 * implementation. The hash code is defined as followed:
	 *
	 * [code]
	 * int hashCode = 1;
	 * final Iterator<E> it = seq.iterator();
	 * while (it.hasNext()) {
	 *     final E obj = it.next();
	 *     hashCode = 31*hashCode + (obj == null ? 0 : obj.hashCode());
	 * }
	 * [/code]
	 *
	 * @see Seq#hashCode()
	 * @see List#hashCode()
	 *
	 * @param seq the sequence to calculate the hash code for.
	 * @return the hash code of the given sequence.
	 */
	public static int hashCode(final Seq<?> seq) {
		int hash = 1;
		for (Object element : seq) {
			hash = 31*hash + (element == null ? 0: element.hashCode());
		}
		return hash;
	}

	/**
	 * Unified method for compare to sequences for equality.
	 *
	 * @see Seq#equals(Object)
	 *
	 * @param seq the sequence to test for equality.
	 * @param obj the object to test for equality with the sequence.
	 * @return {@code true} if the given objects are sequences and contain the
	 *          same objects in the same order, {@code false} otherwise.
	 */
	public static boolean equals(final Seq<?> seq, final Object obj) {
		if (obj == seq) {
			return true;
		}
		if (!(obj instanceof Seq<?>)) {
			return false;
		}

		final Seq<?> other = (Seq<?>)obj;
		boolean equals = (seq.length() == other.length());
		for (int i = seq.length(); equals && --i >= 0;) {
			final Object element = seq.get(i);
			if (element != null) {
				equals = element.equals(other.get(i));
			} else {
				equals = other.get(i) == null;
			}
		}
		return equals;
	}

}
