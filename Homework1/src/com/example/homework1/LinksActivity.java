package com.example.homework1;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseQuery.CachePolicy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.homework1.Link;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LinksActivity extends Activity implements OnItemClickListener {
	private EditText mLinkInput;
	private ListView mListView;
	private LinkAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.links);

		Parse.initialize(this, "BmbiDoWmcHnINiTcLUymM6lcP84rHYd5XrfC2gFW",
				"KY1MGCdC2HawkP4msjtNx4Y1V3qNNWNEbSlEfG82");
		getIntent();
		ParseObject.registerSubclass(Link.class);

		updateData();
		mLinkInput = (EditText) findViewById(R.id.link_input);
		mAdapter = new LinkAdapter(this, new ArrayList<Link>());
		mListView = (ListView) findViewById(R.id.links_list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	private boolean isValidUrl(String url) {
		Pattern p = Patterns.WEB_URL;
		Matcher m = p.matcher(url);
		if (m.matches())
			return true;
		else
			return false;
	}

	public void createLink(View v) {
		if ((mLinkInput.getText().length() > 0)
				&& (isValidUrl(mLinkInput.getText().toString()) == true)) {
			Link l = new Link();
			l.setACL(new ParseACL(ParseUser.getCurrentUser()));
			l.setUser(ParseUser.getCurrentUser());
			l.setDescription(mLinkInput.getText().toString());
			l.setVisited(false);
			l.saveEventually();
			mAdapter.insert(l, 0);
			mLinkInput.setText("");
		}
	}

	public void updateData() {
		ParseQuery<Link> query = ParseQuery.getQuery(Link.class);
		query.whereEqualTo("user", ParseUser.getCurrentUser());
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.findInBackground(new FindCallback<Link>() {
			@Override
			public void done(List<Link> links, ParseException error) {
				if (links != null) {
					mAdapter.clear();
					for (int i = 0; i < links.size(); i++) {
						mAdapter.add(links.get(i));
					}
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Link link = mAdapter.getItem(position);
		TextView linkDescription = (TextView) view
				.findViewById(R.id.link_description);
		Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
		myWebLink.setData(Uri.parse("http://"
				+ linkDescription.getText().toString()));
		startActivity(myWebLink);

		link.saveEventually();
	}

}
