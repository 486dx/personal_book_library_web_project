package com.personal.book.library.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.personal.book.library.datalayer.entity.Book;
import com.personal.book.library.datalayer.entity.Category;
import com.personal.book.library.datalayer.entity.User;
import com.personal.book.library.datalayer.model.LikeDegree;
import com.personal.book.library.servicelayer.BookDraftService;
import com.personal.book.library.servicelayer.BookService;
import com.personal.book.library.servicelayer.CategoryService;
import com.personal.book.library.servicelayer.LikeDegreeService;
import com.personal.book.library.servicelayer.UserService;
import com.personal.book.library.servicelayer.exception.ServiceLayerException;
import com.personal.book.library.servicelayer.model.UserSessionSummary;
import com.personal.book.library.util.HttpSessionUtil;

@RestController
@RequestMapping("/book/library/v1")
public class BookLibraryRestController {
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private LikeDegreeService likeDegreeService;
	
	@Autowired
	private BookDraftService bookDraftService;
	
	@Autowired
	private HttpSession httpSession;
	
	
	@RequestMapping(value="/user", method=RequestMethod.POST)
	public @ResponseBody Long createUser(@RequestBody User user) {
		
		return userService.createUser(user);
	}
	
	@RequestMapping(value="/book", method=RequestMethod.POST)
	public @ResponseBody Long createBook(@RequestBody Book book) {
		
		Long userId = HttpSessionUtil.getUserId(httpSession);
		return bookService.createBook(book, userId);
	}
	
	@RequestMapping(value="/book/draft", method=RequestMethod.POST)
	public @ResponseBody Boolean saveBookAsDraft(@RequestBody com.personal.book.library.datalayer.model.Book book) {
		
		Long userId = HttpSessionUtil.getUserId(httpSession);
		return bookDraftService.saveBookAsDraft(book, userId);
	}
	
	@RequestMapping(value="/book/draft", method=RequestMethod.GET)
	public @ResponseBody com.personal.book.library.datalayer.model.Book getDraftBookFromAuthenticatedUser() {
		
		Long userId = HttpSessionUtil.getUserId(httpSession);
		return bookDraftService.findDraftBook(userId);
	}
	
	@RequestMapping(value="/book/list", method=RequestMethod.GET)
	public @ResponseBody List<Book> getSavedBooksOfUser() {
		
		Long userId = HttpSessionUtil.getUserId(httpSession);
		List<Book> books = bookService.prepareBooksOfUser(userId);
		return books;
	}
	
	@Cacheable(value = "usersummary", cacheManager="cacheManager")
	@RequestMapping(value="/user/summary", method=RequestMethod.GET)
	public @ResponseBody UserSessionSummary getUserSummary() {
		
		UserSessionSummary userSummary = HttpSessionUtil.getUserSessionSummary(httpSession);
		
		if(userSummary == null) {
			throw new ServiceLayerException("INVALID-SESSION-ERROR", "Session is not valid!");
		}
		
		return userSummary;
	}
	
	@Cacheable(value = "categories", cacheManager="cacheManager")
	@RequestMapping(value="/categories", method=RequestMethod.GET)
	public @ResponseBody List<Category> getCategories() {
		
		return categoryService.prepareCategories();
	}
	
	@Cacheable(value = "likedegrees", cacheManager="cacheManager")
	@RequestMapping(value="/likedegree/list", method=RequestMethod.GET)
	public @ResponseBody List<LikeDegree> getLikeDegrees() {
		
		return likeDegreeService.prepareLikeDegrees();
	}
}
