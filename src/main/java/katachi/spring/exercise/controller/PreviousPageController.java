package katachi.spring.exercise.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import katachi.spring.exercise.domain.user.model.Cart;
import katachi.spring.exercise.domain.user.model.CartItem;
import katachi.spring.exercise.domain.user.service.ShoppingService;
import katachi.spring.exercise.userwithcode.UserWithCode;

/**
 * ユーザーの前のページへのリダイレクトを管理するコントローラークラスです。
 */
@Controller
public class PreviousPageController {

	@Autowired
	private ShoppingService shoppingService;

	@Autowired
	private Cart cart;

	@Autowired
	private HttpSession session;

	/**
	 * 認証後にユーザーを前のページにリダイレクトします。
	 *
	 * @param request        リクエスト情報を含むオブジェクト
	 * @param authentication 認証情報を含むオブジェクト
	 * @return 前のページまたはホームページへのリダイレクトURL
	 */
	@GetMapping("/redirect")
	public String redirect(HttpServletRequest request, Authentication authentication) {

		// 現在の認証情報を取得
		Authentication authen = SecurityContextHolder.getContext().getAuthentication();

		UserWithCode userDetails = (UserWithCode) authen.getPrincipal();

		// カートのアイテムをユーザーのカートに転送
		cart.transferCartItems(userDetails.getUserId(), cart.getCartList());

		// ユーザーのカートリストを取得して設定
		List<CartItem> cartList = shoppingService.getCartList(userDetails.getUserId());
		cart.setCartList(cartList);

		session.setAttribute("cart", cartList);
		session.setAttribute("totalQuantity", cart.totalQuantity());
		session.setAttribute("totalAmount", cart.totalAmount());

		// 前のURLにリダイレクト
		String prevUrl = (String) session.getAttribute("prevUrl");
		return "redirect:" + (prevUrl != null ? prevUrl : "/");
	}
}
