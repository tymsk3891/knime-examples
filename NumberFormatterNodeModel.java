package org.knime.examples.numberformatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * これは "NumberFormatterNode" の node model 実装サンプルです。
 * 
 * このサンプルノードは単純な数字書式変換 ({@link String#format(String, Object...)}) を実行します。
 * 入力テーブルの double 型列全てにユーザが定義した文字列書式を使います。
 *
 * @author KNIME GmbH, Konstanz, Germany
 */
public class NumberFormatterNodeModel extends NodeModel {

	/**
	 * この logger は KNIME console と KNIME log ファイルに info/warning/error
	 * メッセージを出力するのに使われます。この node model のクラスを提供している
	 * 'NodeLogger.getLogger'を経由して取得します。
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(NumberFormatterNodeModel.class);

	/**
	 * node dialog と node model 間で共有される設定を取得、格納するための設定キーです。
	 * ここでは、ダイアログでユーザが入力する数値書式の文字列がキーとなります。
	 */
	private static final String KEY_NUMBER_FOMAT = "number_format";

	/**
	 * 数値書式の文字列の初期値です。小数点第３位で切り上げます。
	 * 書式の仕様については以下を参照してください。
	 * https://docs.oracle.com/javase/tutorial/java/data/numberformat.html
	 */
	private static final String DEFAULT_NUMBER_FORMAT = "%.3f";

	/**
	 * 共有設定の管理のための設定モデルです。このモデルはダイアログでユーザーが入力した値を保持し、
	 * ユーザーが値を変更したら更新されます。
	 * さらに、共有設定を簡単にロード、セーブするためのメソッドを提供しています。
	 * 次を参照してください。
	 * <br>
	 * {@link #loadValidatedSettingsFrom(NodeSettingsRO)},
	 * {@link #saveSettingsTo(NodeSettingsWO)}). 
	 * <br>
	 * ここでは、数値書式は文字列であるため、SettingsModelStringを使用します。
	 * 全ての共通データ型にモデルが存在します。設定モデルは単純なダイアログの作成にも使用されるため、
	 * {@ link NumberFormatterNodeDialog} のコンストラクター内のコメントも参照してください。
	 */
	private final SettingsModelString m_numberFormatSettings = createNumberFormatSettingsModel();

	/**
	 * node model のコンストラクタ
	 */
	protected NumberFormatterNodeModel() {
		/**
		 * ノードに必要な出力テーブルと入力テーブルの数を指定します。
		 * この場合、1つの入力テーブルと1つの出力テーブルです。
		 */
		super(1, 1);
	}

	/**
	 * 数値書式文字列のための新しい設定モデルを作成するための便利なメソッドです。
	 * このメソッドは {@link NumberFormatterNodeDialog} でも使用されます。
	 * これらの設定モデルは上記で定義したキーを経由して同期します。
	 * 
	 * @return キーと数値書式文字列を含む新規 SettingsModelString
	 */
	static SettingsModelString createNumberFormatSettingsModel() {
		return new SettingsModelString(KEY_NUMBER_FOMAT, DEFAULT_NUMBER_FORMAT);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		/*
		 * ノードの機能は execute メソッド内で実装されます。
		 * この実装は入力テーブルの double 型列をユーザが指定した書式の String 型列に変換します。
		 * この出力は各 double 型列が書式変換された String 型列です。
		 * 単純にするために、この例では double 型以外の列は無視します。
		 * 
		 * サンプルログを出力します。これは KNIME console と KNIME log に出力されます。
		 * 
		 */
		LOGGER.info("This is an example info.");

		/*
		 * 処理対象となる入力データテーブルです。 "inData" 配列にはコンストラクタで指定された数の入力テーブルが含まれます。
		 * 今回は１つだけです。（コンストラクタを見てください。）
		 */
		BufferedDataTable inputTable = inData[0];

		/*
		 * 出力テーブルの Spec を作成します。入力テーブルの各 double 型列に対して、
		 * 出力に1つの変換された String 型列を作成します。
		 * 詳細は "createOutputSpec(...)" の javadoc を見てください。
		 */
		DataTableSpec outputSpec = createOutputSpec(inputTable.getDataTableSpec());

		/*
		 * 実行コンテキストはストレージの最大容量を提供します。
		 * 今回の場合、行を連続的に追加するためのデータコンテナです。
		 * （注釈）このコンテナは任意の大きなデータテーブルを処理できます。必要に応じてディスクにバッファリングします。
		 * 実行コンテキストはフレームワークによって execute メソッドに引数として提供されます。
		 * exec のメソッドを見てください。データテーブルを作成したり変更するための多数の機能が存在しています。
		 */
		BufferedDataContainer container = exec.createDataContainer(outputSpec);

		/*
		 * 入力テーブルの行イテレータを取得します。行イテレータは入力テーブルから１行ずつ返します。
		 */
		CloseableRowIterator rowIterator = inputTable.iterator();

		/*
		 * どのくらいの行数が処理済みなのかのカウンターです。
		 * これはノードの進捗を計算するのに使われます。ノードアイコンの下部のローディングのバーとして表示されます。
		 */
		int currentRowCounter = 0;
		// 入力テーブルの行を反復します。
		while (rowIterator.hasNext()) {
			DataRow currentRow = rowIterator.next();
			int numberOfCells = currentRow.getNumCells();
			/*
			 * 現在の行に出力するセルを収集するリストです。
			 * データ型とセルの数が DataContainer の作成時に使用した DataTableSpec と一致する必要があります。 
			 */
			List<DataCell> cells = new ArrayList<>();
			// 現在の行のセルを反復します。
			for (int i = 0; i < numberOfCells; i++) {
				DataCell cell = currentRow.getCell(i);
				/*
				 * double 型のセルだけ処理します。したがって、現在のセルが DoubleCell.class であるかチェックします。
				 * 入力テーブルの他のデータ型の全てのセルは無視されます。
				 */
				if (cell.getType().getCellClass().equals((DoubleCell.class))) {
					// cell を DoubleCell 型にキャストします。
					DoubleCell doubleCell = (DoubleCell) cell;
					/*
					 * double 型の値をユーザの定義した数値書式に変換します。
					 * この数値書式は上記で作成した settings model から取得します。
					 */
					String format = m_numberFormatSettings.getStringValue();
					String formatedValue = String.format(format, doubleCell.getDoubleValue());
					// 新しい StringCell を作成し、cell リストに加えます。
					cells.add(new StringCell(formatedValue));
				}
				/*
				 * このサンプルでは、欠損値 cell のチェックをしません。欠損値 cell が行に存在した場合、
				 * ノードは例外処理を発生させます。何故ならば、上記で作成したデータコンテナの table spec
				 * よりも cell の数が少ない行を作成しようとしたためです。したがって、
				 * ノード実装の際には入力テーブルの欠損値 cell のチェックに留意します。
				 * 次に、適切なメッセージで欠損値 cell を作成するか、欠損値 cell がまったく許可されない場合に、
				 * エラーメッセージで例外を発生させます。
				 * ここでは、「cell.isMissing（）」をチェックする「else if」節でこれを実行できます。
				 * 次に、新しいMissingCellをセルのリストに追加します。
				 */
			}
			// 出力データコンテナに新しい行を追加します。
			DataRow row = new DefaultRow(currentRow.getKey(), cells);
			container.addRowToTable(row);

			// １行処理が終わったら、カウンターを１つ増やします。
			currentRowCounter++;

			/*
			 * ここではユーザがノードのキャンセルを実行したかどうかをチェックします。
			 * キャンセルが実行されると、この呼び出しが例外を発生させ、実行が停止します。
			 * このチェックは可能であれば、１行処理が終わる毎に実施しましょう。
			 */
			exec.checkCanceled();

			/*
			 * 実行進捗のパーセンテージの計算を計算し、ExecutionMonitor に提供します。
			 * 加えて、ノードが今何をしているかのメッセージを設定できます。
			 * （このメッセージはノードのプログレスバーをマウスオーバーするとツールチップとして表示されます。）
			 * これは長時間実行中のノードの実行状態を提供するのに特に便利です。
			 */
			exec.setProgress(currentRowCounter / (double) inputTable.size(), "Formatting row " + currentRowCounter);
		}

		/*
		 * 完了したら、コンテナを閉じ、テーブルを返します。コンストラクタで定義したのと同じ数のテーブルを
		 * 返す必要があります。このノードは１つの出力なので、１つのテーブル（テーブルの配列）を返します。
		 */
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		/*
		 * ノードが実行可能かチェックします。例えば、必要なユーザパラメータが全て利用可能で有効か
		 * もしくは入力された型がノードが実行のために適しているか。
		 * ノードが現在の入力を使用して現在の構成で実行できる場合、このノードの実行結果として生じる
		 * table spec を計算して返します。すなわち、このメソッドは出力テーブルの table spec を
		 * 事前に計算します。
		 * 
		 * ここで入力された数値書式文字列の健全さチェックを実施します。
		 * この場合、ダミーの double 数値にその書式を適用してみて、ここで問題があれば、
		 * IllegalFormatException を発生させ、参考情報のメッセージと共に
		 * InvalidSettingsException で捕捉します。
		 * メッセージは何が問題でどうすれば修正できるかを明確にしましょう。
		 * このメッセージは KNIME console に表示され、KNIME log に出力されます。
		 * また、ログには stack trace も含まれます。
		 */
		String format = m_numberFormatSettings.getStringValue();
		try {
			String.format(format, 0.0123456789);
		} catch (IllegalFormatException e) {
			throw new InvalidSettingsException(
					"The entered format is not a valid pattern String! Reason: " + e.getMessage(), e);
		}

		/*
		 * 実行メソッドの戻り値の型と同様に、ノードの出力ポートの数（コンストラクタ内で定義している）分の
		 * DataTableSpec の配列を戻す必要があります。execute メソッドで作成された結果テーブルはこのメソッドで
		 * 作成された spec と一致しなければいけません。新しいデータコンテナを作成するために execute メソッド内で
		 * 再度出力テーブル spec を計算する必要があるので、新しいメソッドを作成します。
		 */
		DataTableSpec inputTableSpec = inSpecs[0];
		return new DataTableSpec[] { createOutputSpec(inputTableSpec) };
	}

	/**
	 * 入力テーブル spec から出力テーブル spec を作成する。入力テーブルの各 double 型列に対し、
	 * String 型で形成された double 値を含む１つの String 型列が作成されます。
	 * @param inputTableSpec
	 * @return
	 */
	private DataTableSpec createOutputSpec(DataTableSpec inputTableSpec) {
		List<DataColumnSpec> newColumnSpecs = new ArrayList<>();
		// 入力列の spec 列を反復する。
		for (int i = 0; i < inputTableSpec.getNumColumns(); i++) {
			DataColumnSpec columnSpec = inputTableSpec.getColumnSpec(i);
			/*
			 * もしも列が double 型列（double 型のセル）だった場合、String 型列と新しい列名から成る新しい
			 * DataColumnSpec を作成します。ここで、元の列名を「Formatted(...)」で囲います。
			 */
			if (columnSpec.getType().getCellClass().equals(DoubleCell.class)) {
				String newName = "Formatted(" + columnSpec.getName() + ")";
				DataColumnSpecCreator specCreator = new DataColumnSpecCreator(newName, StringCell.TYPE);
				newColumnSpecs.add(specCreator.createSpec());
			}
		}

		// DataColumnSpec のリストから新しい DataTableSpec を作成し、戻り値として返します。
		DataColumnSpec[] newColumnSpecsArray = newColumnSpecs.toArray(new DataColumnSpec[newColumnSpecs.size()]);
		return new DataTableSpec(newColumnSpecsArray);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		/*
		 * ユーザ設定を NodeSettings オブジェクトに保存します。SettingsModels は下記のメソッド呼び出しによって
		 * NodeSettings オブジェクトに自身を保存する方法を知っています。一般的に、NodeSettings オブジェクトは
		 * ただのキーと値の格納場所で、全ての共通データ型を書き込むためのメソッドを有しています。したがって、
		 * 設定を自由に書き込むことができます。NodeSettingsWO メソッドを参照してください。
		 */
		m_numberFormatSettings.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		/*
		 * NodeSettings オブジェクトから有効な設定をロードします。
		 * その設定が下記のメソッドによって検証されることは確実に想定できます。
		 * 
		 * SettingModel はローディングを操作します。
		 * この呼び出しの後、（View から）現在の値を SettingModel から取得できます。
		 */
		m_numberFormatSettings.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		/*
		 * 設定が model に適用できるかどうかチェックします。例えば、ユーザーが書式文字列を空白にしている場合、
		 * 既にダイアログ中で処理されているので、チェックする必要はありません。実際にはいかなる変数の値もセットしないようにしましょう。
		 * 
		 */
		m_numberFormatSettings.validateSettings(settings);
	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		/*
		 * 応用的なメソッドです。普通は空白にしておきます。出力ポートに渡される全てのものは自動的にロードされます。
		 * （execute メソッドによって返されたデータ、loadModelContent でロードされた model、loadSettingsFrom を通じて設定されたユーザ設定）
		 * 復元する必要がある内部構造（View で使用されるデータなど）のみをロードします。
		 */
	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		/*
		 * 応用的なメソッドです。普通は空白にしておきます。出力ポートに書き出される全てのものは自動的にセーブされます。
		 * （execute メソッドによって返されたデータ、saveModelContent でセーブされた model、saveSettingsTo を通じて設定されたユーザ設定）
		 * 保存する必要がある内部構造（View で使用されるデータなど）のみをセーブします。
		 */
	}

	@Override
	protected void reset() {
		/*
		 * ノードのリセット時に実行されるコードです。実行中に構築された model はクリアされ、
		 * loadInternals/saveInternals で処理されたデータは削除されます。
		 */
	}
}
