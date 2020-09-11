package com.wecamp.service.owner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.wecamp.mapper.OwnerMapper;
import com.wecamp.model.CampAndSortAndImg;
import com.wecamp.model.Img;
import com.wecamp.model.Inquiry;
import com.wecamp.model.Member;
import com.wecamp.model.Owner;
import com.wecamp.model.Sort;
import com.wecamp.setting.Path;
import com.wecamp.setting.WebTitle;
import com.wecamp.utils.FileUtil;
import com.wecamp.vo.OwnerDetailVo;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@AllArgsConstructor
class OwnerServiceImpl implements OwnerService{
	private OwnerMapper ownerMapper;

	@Override
	public ModelAndView submitInquiryService(Inquiry inquiry) {
		ModelAndView response = new ModelAndView();

		HashMap<String, Object> query = new HashMap<String, Object>();
		query.put("query", inquiry);
		boolean result = ownerMapper.insertInquiry(query);
		response.addObject("result", result);
		return response;
	}

	@Override
	public ModelAndView checkOwner(HttpSession session) {
		Member user  = (Member)session.getAttribute("member");
		ModelAndView response = new ModelAndView();
		response.setViewName("client/member/add_camp/"+WebTitle.TITLE+"캠핑장 등록");
		if(user == null) {
			response.addObject("result", false);
		}else if(user.getA_no() == 1) {
			response.addObject("result", false);
		}else if(user.getA_no() == 2) {
			response.addObject("result", true);
		}
		return response;
	}

	@Transactional
	@Override
	public ModelAndView addCampService(CampAndSortAndImg model, HttpSession session) {
		ModelAndView response = new ModelAndView();
		Member user = (Member)session.getAttribute("member");
		FileUtil fileUtil = new FileUtil();
		HashMap<String, Object> query = new HashMap<String, Object>();
		log.info("#> camp_idx(before) : "+model.getCamp().getCamp_idx());
		if(ownerMapper.insertCamp(model.getCamp())) {
			log.info("#> camp_idx(after) : "+model.getCamp().getCamp_idx());
			query.put("camp", model.getCamp());
			query.put("email", user.getEmail());
			if(ownerMapper.updateOwnerCampIdx(query)) {
				List<Img> imgList =  new ArrayList<Img>();
				String savedName = fileUtil.uploadFile(model.getImgThumb(), Path.CAMP_IMG_THUMB);
				imgList.add(new Img(0, 0, savedName, model.getImgThumb().getOriginalFilename(), model.getImgThumb().getSize(), "thumb"));
				for(MultipartFile file : model.getImgDetail()) {
					imgList.add(new Img(0, 0, fileUtil.uploadFile(file, Path.CAMP_IMG_DETAIL), file.getOriginalFilename(), file.getSize(), "detail"));
				}
				query.put("list", imgList);
				if(ownerMapper.insertImgs(query)) {
					query.remove("list");
					for(Sort sort : model.getSort()) {
						sort.setFname(fileUtil.uploadFile(sort.getSite_img(), Path.CAMP_IMG_SORT));
						sort.setOfname(sort.getSite_img().getOriginalFilename());
					}
					query.put("list", model.getSort());
					if(ownerMapper.insertSorts(query)) { 
						response.addObject("response", true);
					} else {
						response.addObject("response", false);
					}
				} else {
					response.addObject("response", false);
				}
			} else {
				response.addObject("response", false);
			}
		} else {
			response.addObject("response", false);
		}
		response.setViewName("client/member/add_camp/"+WebTitle.TITLE+"캠핑장 등록");
		return response;
	}

	//마이페이지 내 사업자 정보 디테일
	@Override
	public ModelAndView get_owner_full_detail(HttpSession session) {
		Member member = (Member)session.getAttribute("member");
		Owner owner = ownerMapper.select_owner(member.getEmail());
		Integer total_heart = ownerMapper.select_heart(owner.getCamp_idx());
		if(total_heart == null) {
			total_heart = 0;
		}
		OwnerDetailVo result = new OwnerDetailVo(
				ownerMapper.select_camp(owner.getCamp_idx()),
				owner,
				ownerMapper.select_img(owner.getCamp_idx()),
				ownerMapper.select_sort(owner.getCamp_idx()),
				total_heart,
				ownerMapper.select_img_thumb(owner.getCamp_idx())
				);
		ModelAndView response =  new ModelAndView();
		return response.addObject("vo", result);
	}
	
	//문제 1. 예약내역이 사라지는 마술~ -> 사업자가 돈받고 튈수있어 <<-???
	// 	  2. 예약내역 남겨놔 -> 캠핑장 정보를 못가져
	//선택 -> 시연 안 함 (오라클 문제)
	@Transactional
	@Override
	public ModelAndView delete_camp_service(int camp_idx, HttpSession session) {
		ModelAndView response = new ModelAndView();
		Member member = (Member)session.getAttribute("member");
		int i = ownerMapper.update_owner_camp_idx(member.getEmail());
		if(i > 0) {
			log.info("#> checkcheck!!");
			int j = ownerMapper.delete_camp(camp_idx);
		}
		return response;
	}
	
	@Transactional
	@Override
	public ModelAndView update_camp_service(CampAndSortAndImg request) {
		ModelAndView response = new ModelAndView();
		HashMap<String, Object> query = new HashMap<String, Object>();
		
		query.put("camp", request.getCamp());
		if(ownerMapper.update_camp(query)) {
			Img img = ownerMapper.select_img_thumb(request.getCamp().getCamp_idx());
			File file = new File(Path.CAMP_IMG_THUMB, img.getFname());
			file.delete();
			List<Img> img_list = ownerMapper.select_img(request.getCamp().getCamp_idx());
			for(Img temp : img_list) {
				file = new File(Path.CAMP_IMG_DETAIL, temp.getFname());
				file.delete();
			}
			if(ownerMapper.delete_img(query)) {
				List<Sort> sort_list = ownerMapper.select_sort(request.getCamp().getCamp_idx());
				for(Sort sort : sort_list) {
					file = new File(Path.CAMP_IMG_SORT, sort.getFname());
					file.delete();
				}
				if(ownerMapper.delete_sort(query)) {
					FileUtil fileUtil = new FileUtil();
					List<Img> new_img_list = new ArrayList<Img>();
					
					String fname = fileUtil.uploadFile(request.getImgThumb(), Path.CAMP_IMG_THUMB);
					new_img_list.add(new Img(-1, -1, fname, request.getImgThumb().getOriginalFilename(), request.getImgThumb().getSize(), "thumb"));
					
					for(MultipartFile mf : request.getImgDetail()) {
						String d_fname = fileUtil.uploadFile(mf, Path.CAMP_IMG_DETAIL);
						new_img_list.add(new Img(-1, -1, d_fname, mf.getOriginalFilename(), mf.getSize(), "detail"));
					}
					query.put("list", new_img_list);
					if(ownerMapper.insertImgs(query)) {
						for(Sort new_sort : request.getSort()) {
							String s_fname = fileUtil.uploadFile(new_sort.getSite_img(), Path.CAMP_IMG_SORT);
							new_sort.setFname(s_fname);
							new_sort.setOfname(new_sort.getSite_img().getOriginalFilename());
						}
						query.put("list", request.getSort());
						if(ownerMapper.insertSorts(query)) {
							response.addObject("result", true);
							return response;
						}
					}
				}
			}
		}
		response.addObject("result", false);
		return response;
	}
	
}
