package com.example.mvvmappapplication.ui.slideshow;

import android.app.Application;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mvvmappapplication.data.githubcontract.RepositoryListViewContract;
import com.example.mvvmappapplication.data.githubmodel.GitHubService;
import com.example.mvvmappapplication.utils.SingleLiveEvent;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SlideshowViewModel extends AndroidViewModel {
    public final ObservableInt progressBarVisibility = new ObservableInt(View.VISIBLE);
    private RepositoryListViewContract repositoryListView;

    private MutableLiveData<GitHubService.Repositories> repositoriesMutableLiveData;
    //GitHubService Model에 접근한다.
    private  GitHubService gitHubService;
    private MutableLiveData<String> mText;
    SingleLiveEvent<Throwable> errorEvent;
    @Inject
    public SlideshowViewModel(@NonNull Application application,
                              @Named("githubService") GitHubService githubService,
                              @Named("errorEvent") SingleLiveEvent<Throwable> errorEvent) {
        super(application);
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
        this.gitHubService = githubService;
        this.errorEvent = errorEvent;
        repositoriesMutableLiveData = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<GitHubService.Repositories> getRepositoriesMutableLiveData() {
        return repositoriesMutableLiveData;
    }

    public void onLanguageSpinnerItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        loadRepositories((String) parent.getItemAtPosition(position));
    }

    /**
     * 지난 일주일간 만들어진 라이브러리를 인기순으로 가져온다
     */
    private void loadRepositories(String langugae) {
        // 로딩 중이므로 진행바를 표시한다
        progressBarVisibility.set(View.VISIBLE);

        // 일주일 전 날짜 문자열 지금이 2016-10-27이라면 2016-10-20이라는 문자열을 얻는다
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        String text = DateFormat.format("yyyy-MM-dd", calendar).toString();

        // Retrofit을 이용해 서버에 액세스한다

        // 지난 일주일간 만들어졌고 언어가 language인 것을 쿼리로 전달한다
        Observable<GitHubService.Repositories> observable = gitHubService.listRepos("language:" + langugae + " " + "created:>" + text);
        // 입출력(IO)용 스레드로 통신하고, 메인스레드로 결과를 받도록 한다

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(repositories -> {
                    // 로딩이 끝났으므로 진행바를 표시하지 않는다
                    progressBarVisibility.set(View.GONE);
                    // 가져온 아이템을 표시하고자, RecyclerView에 아이템을 설정해 갱신한다
                    repositoriesMutableLiveData.setValue(repositories);
                    //repositoryListView.showRepositories(repositories);
                },throwable -> {
                    // 통신에 실패하면 호출된다
                    // 여기서는 스낵바를 표시한다(아래에 표시되는 바)
                    errorEvent.setValue(throwable);
                });
    }
}