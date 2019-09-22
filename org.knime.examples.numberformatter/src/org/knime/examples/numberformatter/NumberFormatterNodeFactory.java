package org.knime.examples.numberformatter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * これは「NumberFormatterNode」の node factory の実装例です。
 * 
 * node factory は、ノードを構成するすべてのクラスを作成します。さらにノードが view または dialog （もしくは両方）
 * を持つかどうか明記します。
 *
 * @author KNIME GmbH, Konstanz, Germany
 */
public class NumberFormatterNodeFactory extends NodeFactory<NumberFormatterNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NumberFormatterNodeModel createNodeModel() {
		// Create and return a new node model.
		// 新しい node model を作成し、戻り値として返します。
		return new NumberFormatterNodeModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNrNodeViews() {
		// ノードが持つべき view の数、この場合はありません。
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<NumberFormatterNodeModel> createNodeView(final int viewIndex,
			final NumberFormatterNodeModel nodeModel) {
		// We return null as this example node does not provide a view. Also see "getNrNodeViews()".
		// このサンプルノードは view を持たないので、null を返します。「getNrNodeViews()」も参照してください。
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasDialog() {
		// ノードがダイアログを持つかどうかを表します。
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeDialogPane createNodeDialogPane() {
		// このサンプルノードはダイアログを持ちます。したがってダイアログを作成し、返します。「hasDialog()」も参照してください。
		return new NumberFormatterNodeDialog();
	}

}
