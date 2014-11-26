package com.example.jgill.myapplication;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.androidplot.LineRegion;
import com.androidplot.ui.*;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.*;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SimpleXYPLOTFragment extends Fragment {

    private static final String NO_SELECTION_TXT = "Touch bar to select.";
    private XYPlot plot;

    private CheckBox series1CheckBox;
    private CheckBox series2CheckBox;
    private Spinner spRenderStyle, spWidthStyle, spSeriesSize;
    private SeekBar sbFixedWidth, sbVariableWidth;

    private XYSeries series1;
    private XYSeries series2;

    private enum SeriesSize {
        TEN,
        TWENTY,
        SIXTY
    }

    // Create a couple arrays of y-values to plot:
    Number[] series1Numbers10 = {2, null, 5, 2, 7, 4, 3, 7, 4, 5};
    Number[] series2Numbers10 = {4, 6, 3, null, 2, 0, 7, 4, 5, 4};
    Number[] series1Numbers20 = {2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3, 6, 3, 9, 3};
    Number[] series2Numbers20 = {4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9};

    Number[] series1Numbers60 = {2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3, 6, 3, 9, 3, 2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3, 6, 3, 9, 3, 2, null, 5, 2, 7, 4, 3, 7, 4, 5, 7, 4, 5, 8, 5, 3};
    private Number[] series2Numbers60 = {4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9, 4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9, 4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9};
    Number[] series1Numbers = series1Numbers10;
    Number[] series2Numbers = series2Numbers10;

    private MyBarFormatter formatter1;

    private MyBarFormatter formatter2;

    private MyBarFormatter selectionFormatter;

    private TextLabelWidget selectionWidget;

    private Pair<Integer, XYSeries> selection;
    private static List series1NumbersList = new ArrayList<Integer>();
    private boolean loaded = false;
    private static Country countryObject;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countryObject = getActivity().getIntent().getParcelableExtra("countryObject");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_simple_xyplot, container, false);

        new MyAsyncTask().execute();
        // updatePlot();

        plot = (XYPlot) v.findViewById(R.id.mySimpleXYPlot);
        series1CheckBox = (CheckBox) v.findViewById(R.id.s1CheckBox);
        series2CheckBox = (CheckBox) v.findViewById(R.id.s2CheckBox);
        spRenderStyle = (Spinner) v.findViewById(R.id.spRenderStyle);
        spWidthStyle = (Spinner) v.findViewById(R.id.spWidthStyle);
        spSeriesSize = (Spinner) v.findViewById(R.id.spSeriesSize);
        sbFixedWidth = (SeekBar) v.findViewById(R.id.sbFixed);
        sbVariableWidth = (SeekBar) v.findViewById(R.id.sbVariable);

        return v;
    }

    private void setup() {

        // initialize our XYPlot reference:

        //
        formatter1 = new MyBarFormatter(Color.argb(200, 100, 150, 100), Color.LTGRAY);
        formatter2 = new MyBarFormatter(Color.argb(200, 100, 100, 150), Color.LTGRAY);
        selectionFormatter = new MyBarFormatter(Color.YELLOW, Color.WHITE);

        selectionWidget = new TextLabelWidget(plot.getLayoutManager(), NO_SELECTION_TXT,
                new SizeMetrics(
                        PixelUtils.dpToPix(100), SizeLayoutType.ABSOLUTE,
                        PixelUtils.dpToPix(100), SizeLayoutType.ABSOLUTE),
                TextOrientationType.HORIZONTAL);

        selectionWidget.getLabelPaint().setTextSize(PixelUtils.dpToPix(16));

        // add a dark, semi-transparent background to the selection label widget:
        Paint p = new Paint();
        p.setARGB(100, 0, 0, 0);
        selectionWidget.setBackgroundPaint(p);

        selectionWidget.position(
                0, XLayoutStyle.RELATIVE_TO_CENTER,
                PixelUtils.dpToPix(45), YLayoutStyle.ABSOLUTE_FROM_TOP,
                AnchorPosition.TOP_MIDDLE);
        selectionWidget.pack();


        // reduce the number of range labels
        plot.setTicksPerRangeLabel(1);

        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);

        plot.getGraphWidget().setGridPadding(30, 10, 30, 0);

        plot.setTicksPerDomainLabel(1);

        // setup checkbox listers:

        series1CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS1CheckBoxClicked(b);
            }
        });


        series2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS2CheckBoxClicked(b);
            }
        });
        series2CheckBox.setChecked(false);

        plot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()));
                }
                return true;
            }
        });


        ArrayAdapter<BarRenderer.BarRenderStyle> adapter = 
                new ArrayAdapter<BarRenderer.BarRenderStyle>(getActivity(), android.R.layout.simple_spinner_item, 
                        BarRenderer.BarRenderStyle.values());
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRenderStyle.setAdapter(adapter);
        spRenderStyle.setSelection(BarRenderer.BarRenderStyle.SIDE_BY_SIDE.ordinal());
        spRenderStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                updatePlot();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        ArrayAdapter<BarRenderer.BarWidthStyle> adapter1 = new ArrayAdapter<BarRenderer.BarWidthStyle>(getActivity(), 
                android.R.layout.simple_spinner_item, BarRenderer.BarWidthStyle.values());
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWidthStyle.setAdapter(adapter1);
        spWidthStyle.setSelection(BarRenderer.BarWidthStyle.FIXED_WIDTH.ordinal());
        spWidthStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (BarRenderer.BarWidthStyle.FIXED_WIDTH.equals(spWidthStyle.getSelectedItem())) {
                    sbFixedWidth.setVisibility(View.VISIBLE);
                    sbVariableWidth.setVisibility(View.INVISIBLE);
                } else {
                    sbFixedWidth.setVisibility(View.INVISIBLE);
                    sbVariableWidth.setVisibility(View.VISIBLE);
                }
                updatePlot();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        ArrayAdapter<SeriesSize> adapter11 = new ArrayAdapter<SeriesSize>(getActivity(), 
                android.R.layout.simple_spinner_item, SeriesSize.values());
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSeriesSize.setAdapter(adapter11);
        spSeriesSize.setSelection(SeriesSize.SIXTY.ordinal());
        spSeriesSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                switch ((SeriesSize) arg0.getSelectedItem()) {
                    case TEN:
                        series1Numbers = series1Numbers10;
                        series2Numbers = series2Numbers10;
                        break;
                    case TWENTY:
                        series1Numbers = series1Numbers20;
                        series2Numbers = series2Numbers20;
                        break;
                    case SIXTY:

                        series1Numbers = series1Numbers60;
                        series2Numbers = series2Numbers60;
                        break;
                    default:
                        break;
                }
                updatePlot();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        sbFixedWidth.setProgress(5);
        sbFixedWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updatePlot();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        sbVariableWidth.setProgress(1);
        sbVariableWidth.setVisibility(View.INVISIBLE);
        sbVariableWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updatePlot();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        String[] domain = new String[90];

        for (int i = 0; i < domain.length; i++) {
            domain[i] = Integer.toString(i + 1960);
            domain[i] = domain[i] + "-";

        }
        final String[] domainMap = domain;

        //final String[] domainMap =  {"1960", "1960", "333", "444", "555", "666", "666", "666", "666", "666", "666", "666"};
        //Number[] series2Numbers60 = {4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9, 4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9, 4, 6, 3, null, 2, 0, 7, 4, 5, 4, 9, 6, 2, 8, 4, 0, 7, 4, 7, 9};
        plot.setDomainValueFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
                int year = (int) ((value + 0.5d) / 12) + 1960;
                int month = (int) ((value + 0.5d) % 12);
                return new StringBuffer(" " + value);
            }

            @Override
            public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }

            @Override
            public Number parse(String string, ParsePosition position) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });
    }

    private void updatePlot() {

        // Remove all current series from each plot
        Iterator<XYSeries> iterator1 = plot.getSeriesSet().iterator();
        while (iterator1.hasNext()) {
            XYSeries setElement = iterator1.next();
            plot.removeSeries(setElement);
        }

        // Setup our Series with the selected number of elements
        //  series1== new SimpleXYSeries()
        series1 = new SimpleXYSeries(series1NumbersList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, countryObject.getId());
        series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Them");

        // add a new series' to the xyplot:
        if (series1CheckBox.isChecked()) plot.addSeries(series1, formatter1);
        if (series2CheckBox.isChecked()) plot.addSeries(series2, formatter2);

        // Setup the BarRenderer with our selected options
        MyBarRenderer renderer = ((MyBarRenderer) plot.getRenderer(MyBarRenderer.class));
        renderer.setBarRenderStyle((BarRenderer.BarRenderStyle) spRenderStyle.getSelectedItem());
        renderer.setBarWidthStyle((BarRenderer.BarWidthStyle) spWidthStyle.getSelectedItem());
        renderer.setBarWidth(sbFixedWidth.getProgress());
        renderer.setBarGap(sbVariableWidth.getProgress());

        if (BarRenderer.BarRenderStyle.STACKED.equals(spRenderStyle.getSelectedItem())) {
            plot.setRangeTopMin(15);
        } else {
            plot.setRangeTopMin(0);
        }

        plot.redraw();

    }

    private void onPlotClicked(PointF point) {

        // make sure the point lies within the graph area.  we use gridrect
        // because it accounts for margins and padding as well. 
        if (plot.getGraphWidget().getGridRect().contains(point.x, point.y)) {
            Number x = plot.getXVal(point);
            Number y = plot.getYVal(point);


            selection = null;
            double xDistance = 0;
            double yDistance = 0;

            // find the closest value to the selection:
            for (XYSeries series : plot.getSeriesSet()) {
                for (int i = 0; i < series.size(); i++) {
                    Number thisX = series.getX(i);
                    Number thisY = series.getY(i);
                    if (thisX != null && thisY != null) {
                        double thisXDistance =
                                LineRegion.measure(x, thisX).doubleValue();
                        double thisYDistance =
                                LineRegion.measure(y, thisY).doubleValue();
                        if (selection == null) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance < xDistance) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance == xDistance &&
                                thisYDistance < yDistance &&
                                thisY.doubleValue() >= y.doubleValue()) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        }
                    }
                }
            }

        } else {
            // if the press was outside the graph area, deselect:
            selection = null;
        }

        if (selection == null) {
            selectionWidget.setText(NO_SELECTION_TXT);
        } else {
            selectionWidget.setText("Selected: " + selection.second.getTitle() +
                    " Value: " + selection.second.getY(selection.first));
        }
        plot.redraw();
    }

    private void onS1CheckBoxClicked(boolean checked) {
        if (checked) {
            plot.addSeries(series1, formatter1);
        } else {
            plot.removeSeries(series1);
        }
        plot.redraw();
    }

    private void onS2CheckBoxClicked(boolean checked) {
        if (checked) {
            plot.addSeries(series2, formatter2);
        } else {
            plot.removeSeries(series2);
        }
        plot.redraw();
    }

    class MyBarFormatter extends BarFormatter {
        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer getRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        /**
         * Implementing this method to allow us to inject our
         * special selection formatter.
         *
         * @param index  index of the point being rendered.
         * @param series XYSeries to which the point being rendered belongs.
         * @return
         */
        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            if (selection != null &&
                    selection.second == series &&
                    selection.first == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }


    private class MyAsyncTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            // HTTP Client that supports streaming uploads and downloads

            ///
            //define the Url for the task// need to Create a queryBUilder for the Urls

            String url = "http://api.worldbank.org/countries/" + countryObject.getId() + "/indicators/SP.POP.TOTL?date=1960:2009&format=json";

            ////

            InputStream inputStream = null;

            String jsonTextFromWorldBank = null;
            try {

                //    HttpPost httpPost = new HttpPost(url);
                //   httpPost = new HttpPost(url);
                //    httpPost.setHeader("Content-type", "application/json");
                //  System.out.println("dd" + countryObject.getId());
                // System.out.println(url + "kkk");
                //  HttpResponse httpResponse = defaultHttpClient.execute(httpPost);

                //  HttpEntity httpEntity = httpResponse.getEntity();

                // inputStream = httpEntity.getContent();
                inputStream = new URL(url).openStream();


                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                jsonTextFromWorldBank = bufferedReader.readLine();


                //     System.out.print(jsonTextFromWorldBank + "1234567");
                Log.d("urls", url);


            } catch (ClientProtocolException e) {
                System.out.println("d");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (Exception e) {
                }
            }

//            getActionBar().setTitle(jsonTextFromWorldBank);


            return jsonTextFromWorldBank;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("resString", result);
            try {
                series1NumbersList.clear();
                ArrayList<Integer> tempList = new ArrayList<Integer>();
                result = result.substring(result.indexOf('['));
                result = result.substring(1, result.length() - 2);
                String[] parts = result.split(",\\{");
                JSONObject jsonObjectPopulationData = null;
                try {
                    int index = 49;
                    for (String population : parts) {

                        if (!population.startsWith("{")) {

                            population = "{" + population;

                        } else {
                            population = population.substring(result.indexOf('[') + 1);
                        }

                        jsonObjectPopulationData = new JSONObject(population);
                        int value = Integer.parseInt(jsonObjectPopulationData.getString("value"));
                        tempList.add(value);
                        series1Numbers60[index] = value;
                        //JSONObject

                        index--;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  createListView();
                for (int i = tempList.size() - 1; i > 0; i--) {
                    series1NumbersList.add(tempList.get(i));
                }

                setup();
            } catch (StringIndexOutOfBoundsException e) {

            }

        }

    }
}
