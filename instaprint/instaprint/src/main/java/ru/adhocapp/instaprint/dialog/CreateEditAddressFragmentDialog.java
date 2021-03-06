package ru.adhocapp.instaprint.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import ru.adhocapp.instaprint.R;
import ru.adhocapp.instaprint.db.entity.Address;
import ru.adhocapp.instaprint.util.Const;

/**
 * Created by Lenovo on 17.03.14.
 */
public class CreateEditAddressFragmentDialog extends DialogFragment {
    private MapPositiveNegativeClickListener clickListener;
    private AddressDialogMode addressDialogMode;
    private Address address;


    public CreateEditAddressFragmentDialog() {
    }

    public static CreateEditAddressFragmentDialog newInstance(MapPositiveNegativeClickListener listener) {
        CreateEditAddressFragmentDialog f = new CreateEditAddressFragmentDialog();
        f.setClickListener(listener);
        f.setAddressDialogMode(AddressDialogMode.CREATE);
        return f;
    }

    public static CreateEditAddressFragmentDialog newInstance(Address address, MapPositiveNegativeClickListener listener) {
        CreateEditAddressFragmentDialog f = new CreateEditAddressFragmentDialog();
        f.setClickListener(listener);
        f.setAddressDialogMode(AddressDialogMode.EDIT);
        f.setAddress(address);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LinearLayout linearLayout =
                (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.create_address, null);
        int title = 0;
        int positive_buton = 0;
        switch (addressDialogMode) {
            case CREATE: {
                title = R.string.create_address_dialog_title;
                positive_buton = R.string.create_button;
                break;
            }
            case EDIT: {
                title = R.string.edit_address_dialog_title;
                positive_buton = R.string.save_button;
                EditText etToFio = (EditText) linearLayout.findViewById(R.id.full_name);
                EditText etToAddress = (EditText) linearLayout.findViewById(R.id.address);
                EditText etToZip = (EditText) linearLayout.findViewById(R.id.zipcode);
                EditText etToCity = (EditText) linearLayout.findViewById(R.id.city);
                EditText etToCountry = (EditText) linearLayout.findViewById(R.id.country);
                etToFio.setText(address.getFullName());
                etToAddress.setText(address.getStreetAddress());
                etToZip.setText(address.getZipCode());
                etToCity.setText(address.getCityName());
                etToCountry.setText(address.getCountryName());
            }
        }


        return builder.setTitle(title).setView(linearLayout).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clickListener.negativeClick();
            }
        }).setPositiveButton(positive_buton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText etToFio = (EditText) linearLayout.findViewById(R.id.full_name);
                EditText etToAddress = (EditText) linearLayout.findViewById(R.id.address);
                EditText etToZip = (EditText) linearLayout.findViewById(R.id.zipcode);
                EditText etToCity = (EditText) linearLayout.findViewById(R.id.city);
                EditText etToCountry = (EditText) linearLayout.findViewById(R.id.country);
                String etToFioStr = (etToFio.getText() != null) ? etToFio.getText().toString() : null;
                String etToAddressStr = (etToAddress.getText() != null) ? etToAddress.getText().toString() : null;
                String etToZipStr = (etToZip.getText() != null) ? etToZip.getText().toString() : null;
                String etToCityStr = (etToCity.getText() != null) ? etToCity.getText().toString() : null;
                String etToCountryStr = (etToCountry.getText() != null) ? etToCountry.getText().toString() : null;
                if (addressDialogMode == AddressDialogMode.CREATE) {
                    //В диалог добавлю позже
                    address = new Address(etToAddressStr, etToCityStr, etToCountryStr, etToZipStr, etToFioStr);
                } else {
                    address.setStreetAddress(etToAddressStr);
                    address.setFullName(etToFioStr);
                    address.setZipCode(etToZipStr);
                    address.setCityName(etToCityStr);
                    address.setCountryName(etToCountryStr);
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(Const.ADDRESS, address);
                clickListener.positiveClick(map);
            }
        }).create();
    }


    public void setClickListener(MapPositiveNegativeClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public MapPositiveNegativeClickListener getClickListener() {
        return clickListener;
    }

    public AddressDialogMode getAddressDialogMode() {
        return addressDialogMode;
    }

    public void setAddressDialogMode(AddressDialogMode addressDialogMode) {
        this.addressDialogMode = addressDialogMode;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
