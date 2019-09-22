/* @(#)$RCSfile$ 
 * $Revision$ $Date$ $Author$
 *
 */
package org.knime.examples.numberformatter;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * eclipse bundle activator のサンプルです。
 * 
 * このクラスは eclipse/plungin メカニズムによって必要なだけなので、ノード開発者はここでは何もしなくて良いはずです。
 * このファイルを移動やリネームするならば、それに応じてプロジェクトの root ディレクトリにある plugin.xml を必ず変更してください。
 * 
 *
 * @author KNIME GmbH, Konstanz, Germany
 */
public class NumberFormatterNodePlugin extends Plugin {
	// The shared instance.
	private static NumberFormatterNodePlugin plugin;

	/**
	 * コンストラクター
	 */
	public NumberFormatterNodePlugin() {
		super();
		plugin = this;
	}

	/**
	 * このメソッドは、プラグインのアクティブ化時に呼び出されます。
	 * 
	 * @param context
	 *            OSGI バンドルコンテキスト
	 * @throws Exception
	 *             このプラグインがスタートできなかった場合
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);

	}

	/**
	 * このメソッドはプラグインが停止された時に呼び出される。
	 * 
	 * @param context
	 *            The OSGI バンドルコンテキスト
	 * @throws Exception
	 *             プラグインが停止しなかった場合
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * 共有されたインスタンスを返す。
	 * 
	 * @return プラグインのシングルトンインスタンス
	 */
	public static NumberFormatterNodePlugin getDefault() {
		return plugin;
	}

}
