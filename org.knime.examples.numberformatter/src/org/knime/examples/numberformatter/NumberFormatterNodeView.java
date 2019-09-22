package org.knime.examples.numberformatter;

import org.knime.core.node.NodeView;

/**
 * これは「NumberFormatterNode」のノード view の実装サンプルです。
 * 
 * このサンプルノードは view を持たないので、本物の view pane を提供しない NodeView クラスの
 * 空のスタブです。
 *
 * @author KNIME GmbH, Konstanz, Germany
 */
public class NumberFormatterNodeView extends NodeView<NumberFormatterNodeModel> {

	/**
	 * 新しい View を作成します。
	 * 
	 * @param nodeModel The model (class: {@link NumberFormatterNodeModel})
	 */
	protected NumberFormatterNodeView(final NumberFormatterNodeModel nodeModel) {
		super(nodeModel);
		// ここで view のコンポーネントを初期化します。
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void modelChanged() {
		// node model から新しい model を取得し、view を更新する。
		NumberFormatterNodeModel nodeModel = (NumberFormatterNodeModel) getNodeModel();
		assert nodeModel != null;
		/*
		 * 実行されていない可能性がある node model に注意してください！
		 * node model から取得するデータは null、空、などあらゆる種類の無効なものである可能性があります。
		 */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onClose() {
		// view を閉じた時の処理です。
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onOpen() {
		// view を開いた時の処理です。
	}
}
