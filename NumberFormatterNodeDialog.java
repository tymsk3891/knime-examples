package org.knime.examples.numberformatter;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * 「NumberFormatterNode」のノードダイアログのサンプル実装例です。
 *
 * このノードダイアログは標準的なコンポーネントでシンプルなダイアログの作成を可能にする
 *  {@link DefaultNodeSettingsPane} から派生しています。
 *  一般的に誰でも Java Swing を使って任意のダイアログを作成できます。
 * 
 * @author KNIME GmbH, Konstanz, Germany
 */
public class NumberFormatterNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * NumberFormatterNode を構成するための新しい dialog pane です。ここで作成されたダイアログは
	 * KNIME Analytics Platform でノードをダブルクリックした時に表示されます。
	 */
	protected NumberFormatterNodeDialog() {
		super();
		/*
		 * DefaultNodeSettingsPane は addDialogComponent(...) メソッドを経由して dialog pane に
		 * シンプルな標準コンポーネントを追加するためのメソッドを提供します。このメソッドは dialog pane
		 * に追加する新しい DialogComponet object を要求します。テキストボックス（DialogComponentString）
		 * や数値スピナー（DialogComponentNumber）などの一般によく使われる定義済みのコンポーネントが
		 * 多数存在します。
		 * 
		 * 
		 * ダイアログコンポーネントはノード設定に設定をロードしたりセーブしたりできる
		 * settings model オブジェクトを経由して接続されます。
		 * ダイアログコンポーネントが受け取るべき入力のタイプに応じて、コンポーネントのコンストラクター
		 * は適した setting model object を必要とします。その上、ダイアログコンポーネントでは、
		 * コンストラクタでコンポーネントの動作を追加で設定できる場合があります。
		 * 例えば、空の入力を許容しないための設定です。（下記を参照）
		 * ここで、ダイアログにおけるロード/セーブは DefaultNodeSettingsPane によって既に処理されています。
		 * settings model に node model 実装時に使用したのと同じキーを使用するのが重要です。（同じオブジェクトである必要はありません。）
		 * node model 実装でやったように、settings model を作成するために package private static メソッド
		 * を使うと良いでしょう。（NumberFormatterNodeModel クラスの createNumberFormatSettingsModel() を参照してください。）
		 * 
		 * ここで、テキストボックスに入力可能な値のラベル文字列を表示するシンプルな String DialogComponent を作成します。
		 * DialogComponentString は空の入力を許可しない追加オプションを有します。
		 * このため、model 実装中に何も心配する必要はありません。
		 */
		
		// 最初に、node model から create メソッドを使って新しい settings model を作成します。
		SettingsModelString stringSettings = NumberFormatterNodeModel.createNumberFormatSettingsModel();
		// ダイアログに新しい String コンポーネントを追加します。
		addDialogComponent(new DialogComponentString(stringSettings, "Number Format", true, 10));
	}
}
