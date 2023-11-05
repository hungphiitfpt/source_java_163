async function changePass() {
	if($('#pass-1').val() == "" || $('#pass-2').val() == ""){
		alert('Bạn không được để trống mật khẩu')
	}else if($('#pass-2').val() != $('#pass-1').val()){
		alert('Xác nhận mật khẩu phải trùng với mật khẩu mới')
	}else{
		 let method = 'post',
	url = `${api_graduation}changePass`,
	params = {passWord: $('#pass-1').val()}
	data = {}
	let res = await axiosTemplate(method, url, params, data);
	sweatAlert(`Bạn đổi pass thành công`, "success")
	}
}