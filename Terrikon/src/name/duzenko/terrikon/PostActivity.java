package name.duzenko.terrikon;

import name.duzenko.terrikon.logic.Gl;
import name.duzenko.terrikon.logic.LoginTask;
import name.duzenko.terrikon.logic.PostCommentTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences.Editor;

public class PostActivity extends Activity {
	
	EditText editLogin, editPassword, editMsg;
	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		Gl.check(this);
		editLogin = (EditText) findViewById(R.id.editLogin);
		editPassword = (EditText) findViewById(R.id.editPassword);
		editMsg = (EditText) findViewById(R.id.editMessage);
		editLogin.setText(Gl.sharedPreferences.getString("login", null));
		editPassword.setText(Gl.sharedPreferences.getString("password", null));
		url = getIntent().getExtras().getString("url");
		if (url == null)
			finish();
		if (Gl.commentToAnswer != null) {
			String s = Gl.commentToAnswer.spannable.toString();
			/*while (true) {
				int i = s.indexOf("<blockquote class=\"bb_quote\">"), j = s.indexOf("</blockquote>", i) + "</blockquote>".length();
				if (i < 0 || j < 0)
					break;
				s = s.substring(0, i) + s.substring(j);
			}*/
			editMsg.setText("[quote]" + Gl.commentToAnswer.user + '\n' + s + "[/quote]\n");
			editMsg.setSelection(editMsg.getText().length());
		}
	}
	
	public void onPostClick(View view) {
		Editor editor = Gl.sharedPreferences.edit();
		editor.putString("login", editLogin.getText().toString());
		editor.putString("password", editPassword.getText().toString());
		editor.commit();
		if (editMsg.getText().length() == 0) {
			Toast.makeText(this, "Террикон не принимает пустые комментарии ;)", Toast.LENGTH_LONG).show();
			return;
		}
		final Dialog dialog = new Dialog(this);
		dialog.setTitle("Авторизация");
		dialog.setCancelable(false);
		dialog.show();
		new LoginTask(editLogin.getText().toString(), editPassword.getText().toString()) {
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (!this.result) {
					Toast.makeText(PostActivity.this, "Ошибка авторизации. Проверьте имя пользователя и пароль", Toast.LENGTH_LONG).show();
					dialog.dismiss();
				} else {
					dialog.setTitle("Отправка данных...");
					new PostCommentTask(url) {
						
						protected void onPostExecute(Void result) {
							if (!this.result) {
								Toast.makeText(PostActivity.this, "Ошибка отправки", Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(PostActivity.this, "Комментарий, наверное, отправлен", Toast.LENGTH_LONG).show();
								setResult(1);
								finish();
							}
							dialog.dismiss();
						};
						
					}.execute(editMsg.getText().toString());
				}
			};
		}.execute();
	}
}
