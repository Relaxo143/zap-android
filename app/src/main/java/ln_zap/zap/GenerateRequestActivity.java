package ln_zap.zap;

import ln_zap.zap.baseClasses.BaseAppCompatActivity;
import ln_zap.zap.interfaces.UserGuardianInterface;

import ln_zap.zap.util.UserGuardian;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;


import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import net.glxn.qrgen.android.QRCode;

public class GenerateRequestActivity extends BaseAppCompatActivity implements UserGuardianInterface {

    private UserGuardian mUG;
    private String mDataToEncode;
    private boolean mOnChain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Receive data from last activity
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            mOnChain = extras.getBoolean("onChain");

        setContentView(R.layout.activity_generate_request);
        mUG = new UserGuardian(this,this);


        if(mOnChain){
            // Show "On Chain" at top
            ImageView ivTypeIcon = findViewById(R.id.requestTypeIcon);
            ivTypeIcon.setImageResource(R.drawable.ic_onchain_black_24dp);
            TextView tvTypeText = findViewById(R.id.requestTypeText);
            tvTypeText.setText(R.string.onChain);

            // Generate data to encode (placeholder for now)
            mDataToEncode = "2N3GLxzXzkg8aH2wFgsRuEdsEcARfN3DbQg";
        }
        else {
            // Generate data to encode (placeholder for now)
            mDataToEncode = "lntb1u1pwq4jtvpp5r8j6shz7z7grpt6j9l3ep30means72nutpqsd9230pcehsqvqd5sdq8w3jhxaqcqzysxqzjc30qekk28rddvpkzc5uwata53rgsvxc7k4cfy3fmrjuwun70m79lq9e3s4aqflxn024v8wsz5cavgarm7qq6tjmdztjuv3jeurgvfayqqgajfl0";
        }



        // Generate "QR-Code"
        Bitmap bmpQRCode = QRCode
                .from(mDataToEncode)
                .withSize(750,750)
                .withErrorCorrection(ErrorCorrectionLevel.L)
                .bitmap();
        ImageView ivQRCode = findViewById(R.id.requestQRCode);
        ivQRCode.setImageBitmap(bmpQRCode);

        // Action when clicked on "QR-Code"
        ivQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUG.securityCopyToClipboard();
            }
        });


        // Action when clicked on "share"
        Button btnShare = findViewById(R.id.requestShareButton);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, mDataToEncode);
                shareIntent.setType("text/plain");
                String title = getResources().getString(R.string.shareDialogTitle);
                startActivity(Intent.createChooser(shareIntent, title));
            }
        });

        // Action when clicked on "details"
        Button btnDetails = findViewById(R.id.requestDetailsButton);
        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(GenerateRequestActivity.this)
                        .setTitle(R.string.details)
                        .setMessage(mDataToEncode)
                        .setCancelable(true)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) { }
                        }).show();
            }
        });

        // Action when clicked on "copy"
        Button btnCopyLink = findViewById(R.id.requestCopyButton);
        btnCopyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUG.securityCopyToClipboard();
            }
        });

    }

    @Override
    public void guardianDialogConfirmed(String DialogName) {
        switch (DialogName) {
            case UserGuardian.COPY_TO_CLIPBOARD:
                copyToClipboard();
                break;
        }
    }

    private void copyToClipboard(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Address", mDataToEncode);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this,R.string.copied_to_clipboard,Toast.LENGTH_SHORT).show();
    }
}
