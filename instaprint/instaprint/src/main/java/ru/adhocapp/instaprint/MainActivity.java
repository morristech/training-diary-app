package ru.adhocapp.instaprint;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import ru.adhocapp.instaprint.util.PageFragment;

public class MainActivity extends FragmentActivity {

    static final String TAG = "myLogs";
    static final int PAGE_COUNT = 4;
    static final int SELECT_FOTO_REQUEST_CODE = 199;
    Bitmap selectedImage;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        //count pages will be stored in buffer
        pager.setOffscreenPageLimit(4);
        pager.setAdapter(pagerAdapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            String title = "";
            switch (position){
                case 0:
                    title = getString(R.string.page_title_select_foto);
                    break;
                case 1:
                    title = getString(R.string.page_title_edit_text);
                   break;
                case 2:
                    title = getString(R.string.page_title_edit_address);
                    break;
                case 3:
                    title = getString(R.string.page_title_result);
                    break;
            }
            return title;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public void clickNexеtSelectFoto(View view){
        pager.setCurrentItem(1);
    }

    public void clickPreviousEditText(View view){
        pager.setCurrentItem(0);
    }

    public void clickNextEditText(View view){
        pager.setCurrentItem(2);
    }

    public void clickPreviousEditAddress(View view){
        pager.setCurrentItem(1);
    }

    public void clickNextEditAddress(View view){
        pager.setCurrentItem(3);
    }

    public void clickPreviousResult(View view){
        pager.setCurrentItem(2);
    }

    public void clickSelectFoto(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_FOTO_REQUEST_CODE);
    }

    public void clickSend(View view){

        EditText etUserText = (EditText)findViewById(R.id.et_user_text);
        EditText etFromFio = (EditText)findViewById(R.id.et_from_fio);
        EditText etFromAddress = (EditText)findViewById(R.id.et_from_address);
        EditText etFromZip = (EditText)findViewById(R.id.et_from_zip);
        EditText etToFio = (EditText)findViewById(R.id.et_to_fio);
        EditText etToAddress = (EditText)findViewById(R.id.et_to_address);
        EditText etToZip = (EditText)findViewById(R.id.et_to_zip);
        String text = selectedImage.toString()
                +"\t"+etUserText.getText().toString()
                +"\t"+etFromFio.getText().toString()
                +"\t"+etFromAddress.getText().toString()
                +"\t"+etFromZip.getText().toString()
                +"\t"+etToFio.getText().toString()
                +"\t"+etToAddress.getText().toString()
                +"\t"+etToZip.getText().toString();
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();

    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_FOTO_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Uri selectedImageURI = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImageURI, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    selectedImage = BitmapFactory.decodeFile(filePath);
                    ImageView imageView = (ImageView)findViewById(R.id.ivUserFoto);
                    imageView.setImageBitmap(selectedImage);

                }
        }
    }
}