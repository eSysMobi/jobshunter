package mobi.esys.custom_edit_format_validators;

import java.util.Calendar;

import mobi.esys.jobshunter.R;
import android.content.Context;
import android.widget.EditText;

import com.andreabaccega.formedittextvalidator.Validator;

public class AgeValidator extends Validator {

	public AgeValidator(Context context) {
		super(context.getResources().getString(R.string.age_validator));
	}

	@Override
	public boolean isValid(EditText et) {
		int indexOfLastDot = et.getText().toString().lastIndexOf(".");
		int birthYear = Integer
				.parseInt(et
						.getText()
						.toString()
						.substring(indexOfLastDot + 1,
								et.getText().toString().length()));
		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		return currentYear - birthYear >= 16;
	}

}
