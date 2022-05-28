package com.example.fitme.ui.rank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitme.R
import com.example.fitme.core.extentions.loadUrl
import com.example.fitme.core.extentions.visible
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.data.models.User
import com.example.fitme.databinding.FragmentRankBinding
import com.example.fitme.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RankFragment : BaseNavFragment<HomeViewModel, FragmentRankBinding>() {

    override val viewModel: HomeViewModel by viewModel()
    private val itemList = ArrayList<User>()

    override fun initViewModel() {
        super.initViewModel()

        viewModel.getUsers().observe(this) { response ->
            when (response.status) {
                Status.LOADING -> {
                    viewModel.loading.postValue(true)
                }
                Status.ERROR -> {
                    viewModel.loading.postValue(false)
                }
                Status.SUCCESS -> {
                    viewModel.loading.postValue(false)
                    response.data?.let {
                        itemList.addAll(it)
                        initRecycler()
                    }
                }
            }
        }

        viewModel.loading.observe(this) {
            binding.loading.visible = it
        }
    }

//         List<Player> players = new ArrayList<Player>() {{
//        add(new Player(1L, "a", 5));
//        add(new Player(2L, "b", 7));
//        add(new Player(3L, "c", 8));
//        add(new Player(4L, "d", 9));
//        add(new Player(5L, "e", 3));
//        add(new Player(6L, "f", 8));
//     }};
//     int[] score = {Integer.MIN_VALUE};
//     int[] no = {0};
//     int[] rank = {0};
//     List<Ranking> ranking = players.stream()
//         .sorted((a, b) -> b.getScores() - a.getScores())
//         .map(p -> {
//             ++no[0];
//             if (score[0] != p.getScores()) rank[0] = no[0];
//             return new Ranking(rank[0], score[0] = p.getScores());
//         })
//         // .distinct() // if you want to remove duplicate rankings.
//         .collect(Collectors.toList());
//     System.out.println(ranking);
//    // result:
//    // rank=1, score=9
//    // rank=2, score=8
//    // rank=2, score=8
//    // rank=4, score=7
//    // rank=5, score=5
//    // rank=6, score=3

    override fun initView() {
        super.initView()
        setMyData()
    }

    private fun setMyData() {
        viewModel.getLocalProfile()?.let { user ->
            binding.ivAvatar.loadUrl(user.image, R.drawable.ic_person_placeholder)
            val text = user.lastName + " " + user.firstName
            binding.tvName.text = text
//            binding.rateNumber.text = user.rank.toString()
        }
    }

    private fun initRecycler() {

        val items = itemList.sortedByDescending {
            it.score
        }

        for ((index, item) in items.withIndex()) {
            item.rank = index + 1
        }

        binding.recyclerView.apply {
            this.adapter = RankAdapter(items) {}
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun initListeners() {
        super.initListeners()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentRankBinding {
        return FragmentRankBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentRankBinding {
        return FragmentRankBinding.bind(view)
    }

}