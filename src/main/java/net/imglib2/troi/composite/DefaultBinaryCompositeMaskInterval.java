package net.imglib2.troi.composite;

import java.util.function.Predicate;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Localizable;
import net.imglib2.troi.BoundaryType;
import net.imglib2.troi.Bounds;
import net.imglib2.troi.MaskInterval;
import net.imglib2.troi.Operators.BinaryMaskOperator;

/**
 * @author Tobias Pietzsch
 */
public class DefaultBinaryCompositeMaskInterval
		extends AbstractWrappedInterval< Bounds.IntervalOrEmpty >
		implements BinaryCompositeMaskPredicate< Localizable >, MaskInterval, Bounds.IntervalOrEmpty
{
	private final BinaryMaskOperator operator;

	private final Predicate< ? super Localizable > arg0;

	private final Predicate< ? super Localizable > arg1;

	private final BoundaryType boundaryType;

	private final Predicate< ? super Localizable > predicate;

	public DefaultBinaryCompositeMaskInterval(
			final BinaryMaskOperator operator,
			final Predicate< ? super Localizable > arg0,
			final Predicate< ? super Localizable > arg1,
			final Bounds.IntervalOrEmpty interval,
			final BoundaryType boundaryType )
	{
		super( interval );
		this.operator = operator;
		this.arg0 = arg0;
		this.arg1 = arg1;
		this.boundaryType = boundaryType;
		this.predicate = operator.predicate( arg0, arg1 );
	}

	@Override
	public BoundaryType boundaryType()
	{
		return boundaryType;
	}

	@Override
	public boolean test( final Localizable localizable )
	{
		return predicate.test( localizable );
	}

	@Override
	public BinaryMaskOperator operator()
	{
		return operator;
	}

	@Override
	public Predicate< ? super Localizable > arg0()
	{
		return arg0;
	}

	@Override
	public Predicate< ? super Localizable > arg1()
	{
		return arg1;
	}

	@Override
	public boolean isEmpty()
	{
		return this.sourceInterval.isEmpty();
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !( obj instanceof BinaryCompositeMaskPredicate ) || !( obj instanceof MaskInterval ) )
			return false;

		final BinaryCompositeMaskPredicate< ? > b = ( BinaryCompositeMaskPredicate< ? > ) obj;
		return b.operator() == operator && arg0.equals( b.arg0() ) && arg1.equals( b.arg1() );
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}
