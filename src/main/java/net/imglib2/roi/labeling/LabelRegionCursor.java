/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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
package net.imglib2.roi.labeling;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.roi.util.iterationcode.IterationCodeListIterator;
import net.imglib2.type.logic.BoolType;

public class LabelRegionCursor extends AbstractLocalizable implements Cursor< BoolType >
{
	private final BoolType type = new BoolType( true );

	private final IterationCodeListIterator< Point > iter;

	public LabelRegionCursor( final ArrayList< TIntArrayList > itcodesList, final long[] offset )
	{
		super( offset.length );
		iter = new IterationCodeListIterator< Point >( itcodesList, offset, Point.wrap( position ) );
	}

	protected LabelRegionCursor( final LabelRegionCursor c )
	{
		super( c.n );
		iter = new IterationCodeListIterator< Point >( c.iter, Point.wrap( position ) );
	}

	@Override
	public BoolType get()
	{
		return type;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		iter.jumpFwd( steps );
	}

	@Override
	public void fwd()
	{
		iter.fwd();
	}

	@Override
	public void reset()
	{
		iter.reset();
	}

	@Override
	public boolean hasNext()
	{
		return iter.hasNext();
	}

	@Override
	public BoolType next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{
		// NB: no action.
	}

	@Override
	public LabelRegionCursor copy()
	{
		return new LabelRegionCursor( this );
	}

	@Override
	public LabelRegionCursor copyCursor()
	{
		return copy();
	}
}
