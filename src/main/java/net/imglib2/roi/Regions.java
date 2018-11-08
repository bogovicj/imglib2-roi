/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2017 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.roi;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.util.IterableRegionOnBooleanRAI;
import net.imglib2.roi.util.PositionableWrappedIterableRegion;
import net.imglib2.roi.util.SamplingIterableInterval;
import net.imglib2.type.BooleanType;
import net.imglib2.view.Views;

public class Regions
{
	// TODO: bind to (respectively sample from) RandomAccessible
	// TODO: out-of-bounds / clipping

	public static < T > IterableInterval< T > sample( final IterableInterval< Void > region, final RandomAccessible< T > img )
	{
		return SamplingIterableInterval.create( region, img );
	}

	/**
	 * Make any {@code RandomAccessibleInterval<BooleanType>} into an
	 * {@code IterableRegion}.
	 * <p>
	 * If {@code region} already is an {@code IterableRegion}, return it.
	 * Otherwise, wrap it. This is potentially expensive, because it requires
	 * iterating {@code region} once.
	 *
	 * @param region
	 *            the region to make iterable
	 * @return {@code region} as an {@code IterableRegion}
	 */
	public static < B extends BooleanType< B > > IterableRegion< B > iterable( final RandomAccessibleInterval< B > region )
	{
		if ( region instanceof IterableRegion )
			return ( IterableRegion< B > ) region;
		else
			return new IterableRegionOnBooleanRAI<>( region );
	}

	/**
	 * Make any {@code RandomAccessibleInterval<BooleanType>} into an
	 * {@code PositionableIterableRegion}.
	 * <p>
	 * If {@code region} already is a {@code PositionableIterableRegion}, return
	 * it. Otherwise, wrap it. This is potentially expensive, because it
	 * requires iterating {@code region} once, unless it already is an
	 * {@code IterableRegion}.
	 *
	 * @param region
	 *            the region to make iterable and positionable
	 * @return {@code region} as an {@code PositionableIterableRegion}
	 */
	public static < B extends BooleanType< B > > PositionableIterableRegion< B > positionable( final RandomAccessibleInterval< B > region )
	{
		if ( region instanceof PositionableIterableRegion )
			return ( PositionableIterableRegion< B > ) region;
		else
			return new PositionableWrappedIterableRegion<>( Regions.iterable( region ) );
	}

	public static < T extends BooleanType< T > > long countTrue( final RandomAccessibleInterval< T > interval )
	{
		long sum = 0;
		for ( final T t : Views.iterable( interval ) )
			if ( t.get() )
				++sum;
		return sum;
	}
}
