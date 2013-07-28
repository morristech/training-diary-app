package myApp.trainingdiary.statistic;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myApp.trainingdiary.R;
import myApp.trainingdiary.db.DBHelper;
import myApp.trainingdiary.db.MeasureFormatter;
import myApp.trainingdiary.db.entity.Exercise;
import myApp.trainingdiary.db.entity.ExerciseType;
import myApp.trainingdiary.db.entity.Measure;
import myApp.trainingdiary.db.entity.TrainingStat;
import myApp.trainingdiary.utils.Consts;

public class StatisticActivity extends Activity {

    private ImageButton settingButton;
    private TextView label;
    private LinearLayout graphLayout;
    private DBHelper dbHelper;
    private Long ex_id;
    private ImageView icon;

    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd.MM.yy");
    private StatisticGraph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        settingButton = (ImageButton) findViewById(R.id.setting_button);
        label = (TextView) findViewById(R.id.label);
        graphLayout = (LinearLayout) findViewById(R.id.graph);
        icon = (ImageView) findViewById(R.id.icon);
        dbHelper = DBHelper.getInstance(this);
        graph = createGraph();
        try {
            ex_id = getIntent().getExtras().getLong(Consts.EXERCISE_ID);
        } catch (NullPointerException e) {
        }

        if (ex_id != null) {
            drawExerciseProgress(ex_id, null, null, null);
        }

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private StatisticGraph createGraph() {
        return new StatisticGraph();
    }

    private void drawExerciseProgress(Long ex_id, Long measure_id, Long group_measure_id, Long group_count) {
        Exercise exercise = dbHelper.READ.getExerciseById(ex_id);
        exercise.getType().getMeasures().addAll(dbHelper.READ.getMeasuresInExercise(ex_id));
        List<TrainingStat> progress = dbHelper.READ.getExerciseProgress(ex_id);
        if (!progress.isEmpty()) {
            label.setText(exercise.getName() + " (" + SDF_DATE.format(progress.get(0).getDate()) + ((progress.size() > 1) ?
                    (" - " + SDF_DATE.format(progress.get(progress.size() - 1).getDate())) : "") + ")");
            icon.setImageResource(getResources()
                    .getIdentifier(exercise.getType().getIcon(),
                            "drawable", getPackageName()));
            Measure measure = null;
            int pos = 0;
            if (measure_id != null) {
                measure = getMeasureById(measure_id, exercise.getType());
                pos = getPosByMeasureId(measure_id, exercise.getType());
            } else {
                measure = exercise.getType().getMeasures().get(0);
            }
            if (group_measure_id == null) {
                addSeries(measure.getName());
                for (TrainingStat stat : progress) {
                    mCurrentSeries.add(stat.getDate().getTime(), MeasureFormatter.getValueByPos(stat.getValue(), pos));
                }
            } else {
                int m_g_pos = getPosByMeasureId(group_measure_id, exercise.getType());
                Measure group_measure = getMeasureById(measure_id, exercise.getType());
                if (m_g_pos == pos) {
                    addSeries(measure.getName());
                    for (TrainingStat stat : progress) {
                        mCurrentSeries.add(stat.getDate().getTime(), MeasureFormatter.getValueByPos(stat.getValue(), pos));
                    }
                } else {
                    Map<String, List<Pair>> map = new HashMap<String, List<Pair>>();
                    for (TrainingStat stat : progress) {
                        String groupValue = MeasureFormatter.toMeasureValues(stat.getValue()).get(m_g_pos);
                        Double value = MeasureFormatter.getValueByPos(stat.getValue(), pos);
                        if (map.containsKey(groupValue)) {
                            map.get(groupValue).add(new Pair(stat.getDate(), value));
                        } else {
                            List list = new ArrayList<Pair>();
                            list.add(new Pair(stat.getDate(), value));
                            map.put(groupValue, list);
                        }
                    }
                    if (group_count == null || group_count < 1) {
                        group_count = 1L;
                    }
                    while (map.size() > group_count) {
                        String minKey = null;
                        int minKeySize = Integer.MAX_VALUE;
                        for (String key : map.keySet()) {
                            if (minKeySize > map.get(key).size()) {
                                minKeySize = map.get(key).size();
                                minKey = key;
                            }
                        }
                        map.remove(minKey);
                    }
                    for (String key : map.keySet()) {
                        addSeries(group_measure.getName() + "_" + key);
                        for (Pair p : map.get(key)) {
                            mCurrentSeries.add(((Date) p.first).getTime(), (Double) p.second);
                        }
                    }
                }
            }
        } else {
            label.setText(exercise.getName() + " - " + getString(R.string.exercise_nothing_to_show));
        }
        graph.repaint();
    }

    private int getPosByMeasureId(Long measure_id, ExerciseType type) {
        for (int i = 0; i < type.getMeasures().size(); i++) {
            if (measure_id == type.getMeasures().get(i).getId()) {
                return i;
            }
        }
        return -1;
    }

    private Measure getMeasureById(Long measure_id, ExerciseType type) {
        for (Measure m : type.getMeasures()) {
            if (measure_id == m.getId()) {
                return m;
            }
        }
        return null;
    }


    private int getColor(int seriesCount) {

        switch (seriesCount) {
            case 0:
                return Color.GREEN;
            case 1:
                return Color.BLUE;
            case 2:
                return Color.RED;
            case 3:
                return Color.GRAY;
            case 4:
                return Color.BLACK;
            case 5:
                return Color.MAGENTA;
            case 6:
                return Color.DKGRAY;
            case 7:
                return Color.YELLOW;
            default:
                return Color.CYAN;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_statistic, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        graph.saveState(outState);
        // save the current data, for instance when changing screen orientation
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        // restore the current data, for instance when changing the screen
        // orientation
        graph.restoreState(savedState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createGraphView();
    }


}