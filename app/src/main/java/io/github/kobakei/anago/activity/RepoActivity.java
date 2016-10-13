package io.github.kobakei.anago.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import io.github.kobakei.anago.R;
import io.github.kobakei.anago.databinding.RepoActivityBinding;
import io.github.kobakei.anago.fragment.IssueListFragment;
import io.github.kobakei.anago.fragment.PullRequestListFragment;
import io.github.kobakei.anago.fragment.RepoInfoFragment;
import io.github.kobakei.anago.viewmodel.RepoInfoViewModel;
import io.github.kobakei.anago.viewmodel.RepoViewModel;

/**
 * リポジトリ詳細画面
 */
public class RepoActivity extends BaseActivity {

    private static final String KEY_USER = "user";
    private static final String KEY_REPO = "repo";

    @Inject
    RepoViewModel viewModel;

    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);
        bindViewModel(viewModel);

        RepoActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.repo_activity);
        binding.setViewModel(viewModel);

        // 変数
        String user = getIntent().getStringExtra(KEY_USER);
        String repo = getIntent().getStringExtra(KEY_REPO);

        // タブ＋ページャー
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return RepoInfoFragment.newInstance(user, repo);
                    case 1:
                        return RepoInfoFragment.newInstance(user, repo);
                    case 2:
                        return IssueListFragment.newInstance(user, repo);
                    case 3:
                        return PullRequestListFragment.newInstance(user, repo);
                    default:
                        return null;
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "About";
                    case 1:
                        return "Code";
                    case 2:
                        return "Issue";
                    case 3:
                        return "Pull Request";
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Subscribe
    public void onRepoRefreshed(RepoInfoViewModel.RefreshRepoEvent event) {
        getSupportActionBar().setTitle(event.repo.name);
    }

    /**
     * このActivityに遷移する
     * @param context
     * @param user
     * @param repo
     */
    public static void startActivity(Context context, String user, String repo) {
        Intent intent = new Intent(context, RepoActivity.class);
        intent.putExtra(KEY_USER, user);
        intent.putExtra(KEY_REPO, repo);
        context.startActivity(intent);
    }
}
