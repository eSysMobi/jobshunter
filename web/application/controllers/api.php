<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

require APPPATH.'/libraries/REST_Controller.php';
class Api extends REST_Controller {

	/**
	 * Index Page for this controller.
	 *
	 * Maps to the following URL
	 * 		http://example.com/index.php/welcome
	 *	- or -  
	 * 		http://example.com/index.php/welcome/index
	 *	- or -
	 * Since this controller is set as the default controller in 
	 * config/routes.php, it's displayed at http://example.com/
	 *
	 * So any other public methods not prefixed with an underscore will
	 * map to /index.php/welcome/<method_name>
	 * @see http://codeigniter.com/user_guide/general/urls.html
	 */
	public function index()
	{
		$this->load->view('welcome_message');
	}
	//Метод для регистрации или входа юзеров
	public function register_user_get()
    {
		if($this->get('network') && $this->get('network_id') && !$this->get('first_name')) { //Если задана соц. сеть и айди, но не задано имя - пытаемся войти
			$u = new User();
			$u->network		= $this->get('network');
			$u->network_id	= $this->get('network_id');
			if ($u->login()) {
				$this->response(array('status' => 'success', 'apikey' => $u->apikey, 'id' => $u->id), 200);
				return true;
			}
		}
		if(!$this->get('last_name') || !$this->get('first_name') || !$this->get('network') || !$this->get('network_id')) {
			$this->response(array('error' => 'Invalid_data'), 404);
		}
		$this->load->model('user');
		$u = new User(); // Регистрируемся
		$ar = $this->user->where('network', $this->get('network'))->where('network_id', $this->get('network_id'))->get()->all_to_array();
		if (!empty($ar)) {
			$this->response(array('error' => 'User exists'), 400);
			return false;
		}
		$u->network = $this->get('network');
		$u->network_id = $this->get('network_id');
		$u->first_name = $this->get('first_name');
		$u->last_name = $this->get('last_name');
		$u->apikey = $u->generate_apikey();
		if ($u->save()) {
			$this->response(array('status' => 'success', 'apikey' => $u->apikey, 'id' => $u->id), 200);
		} else {
			$this->response(array('status' => 'fail'));
		}
	}
	public function update_categories_get() { //Обновляет категории вакансий с сайтов
		$this->load->model('hh_api');
		$this->load->model('sj_api');
		
		if ($this->hh_api->categories_to_db($this->hh_api->get_categories()) && $this->sj_api->categories_to_db($this->sj_api->get_categories())) { //$this->hh_api->categories_to_db($this->hh_api->get_categories())
			$this->response(array('status' => 'success'));
		} else {
			$this->response(array('status' => 'fail'));
		}
	}
	public function temp_get() {
		$this->load->library('gcm');
		// $this->load->model('sj_api');
		// ini_set('default_socket_timeout', 10);
		// $this->sj_api->get_vacancies();
		$this->load->model('hh_api');
		ini_set('default_socket_timeout', 10);
		$this->hh_api->get_vacancies();
	}
	public function vacancies_to_db_get() {
		$this->load->model('sj_api');
		ini_set('default_socket_timeout', 10);
		$this->sj_api->get_vacancies();
		$this->load->model('hh_api');
		$this->hh_api->get_vacancies();	
	}
	public function send_to_subscribers_get() {
		$list = new Vacancy_list;
		$list->load_from_db();
		$this->load->model('subscription');
		$this->subscription->get_subscriptions();
	}
	public function sitecats_get() {
		$this->load->model('site_categories');
		$all = $this->site_categories->get()->all_to_array();
		$this->response($all,200);
	}
	public function sourcecats_get() {
		if(!$this->get('site') && (!$this->get('site')!='hh' && !$this->get('site')!='sj')) {
			$this->response(array('status' => 'Bad request'), 200);
		}
		$site = $this->get('site');
		$obj = new Sourcecategories;
		$result = $obj->where('site',$site)->get();
		$out = array();
		foreach ($result as $result_row) {
			$out[] = array('id' => $result_row->id, 'name' => $result_row->category_name, 'parent_id' => $result_row->parent_id);
		}
		$this->response($out,200);
	}
	// public function citieshh_get() { //Получение городов с HH в базу
		// $this->load->model('hh_api');
		// $this->hh_api->get_cities();
		// $this->response(array('status' => 'success'));
	// }
	// public function sjcities_get() {  // Метод для получения городов с SJ в базу, вместо cities_to_db возможны countries_to_db и regions_to_db
		// $this->load->model('sj_api');
		// if($this->sj_api->cities_to_db()) {
			// $this->response(array('status' => 'success'));
		// } else {
			// $this->response(array('status' => 'fail'));
		// }	
	// }
	public function cities_get() { //Получение городов из базы
		if(!$this->get('str')) {
			$this->response(array('status' => 'Bad string'), 200);
		}
		$this->load->model('city');
		$cities = $this->city->get_cities_by_substr($this->get('str'));
		$out = array('cities' => $cities,'regions' => $this->city->get_parents_for_cities($cities));
		$this->response($out);
	}
	public function vacancies_get() { // Получение вакансий
		if($this->get('page')) {
			$page = $this->get('page');
		} else {
			$page = 0;
		}
		if($this->get('sjcats')) {
			$hhcats = $this->get('sjcats');
		}
		$hh_city_query = '';
		$sj_city_query = '';
		$hh_spec_query = '';
		$sj_spec_query = '';
		$this->load->model('hh_api');
		$this->load->model('sj_api');
		if($this->get('city')) { // Если заданы города
			$cities = $this->get('city');
			$this->load->helper('array');
			$this->load->model('city');
			$hh_cities = array();
			$sj_cities = array();
			foreach($cities as $city) { // Получем из базы ID городов внутри систем SJ/HH
				$city_obj = element(0, $this->city->where('id',$city)->get()->all_to_array());
				if ($city_obj['hh_id']!=0) {
					$hh_cities[] = $city_obj['hh_id'];
				}
				if ($city_obj['sj_type'] && $city_obj['sj_id']) {
					$sj_cities[] = array('id' => $city_obj['sj_id'],'type' => $city_obj['sj_type']);
				}
			}
			//Составляем части для запросов
			$hh_city_query = $this->hh_api->create_query_part($hh_cities,'area');
			
			$first = true;
			foreach($sj_cities as $sj_city) {
				if (!$first) {
					$sj_city_query .='&';
				}
				$sj_city_query .= $sj_city['type'].'[]='.$sj_city['id'];
				$first = false;
			}
		}
		//Если заданы категории для HH - составляем часть запроса
		if($this->get('hhcats')) {
			$hhcats = $this->get('hhcats');
			$hh_spec_query = $this->hh_api->create_query_part($hhcats,'specialization');
			log_message('info','vacancies?'.$hh_city_query.((!empty($hh_city_query) ? '&' : '')).$hh_spec_query.((!empty($hh_spec_query) ? '&' : '')).'page='.$page);
			$hh_answer = json_decode($this->hh_api->make_request('vacancies?'.$hh_city_query.((!empty($hh_city_query) ? '&' : '')).$hh_spec_query.((!empty($hh_spec_query) ? '&' : '')).'page='.$page));
			$hh = $this->hh_api->process_vacancies($hh_answer);
			$hh_more = ($hh_answer->pages-2>=$hh_answer->page);
		} else {
			$hh = array();
			$hh_more = false;
		}
		if($this->get('sjcats')) {
			$sjcats = $this->get('sjcats');
			$sj_spec_query = 'catalogues='.implode(",", $sjcats);
			$sj_answer = json_decode($this->sj_api->make_request('vacancies/?'.$sj_city_query.((!empty($sj_city_query) ? '&' : '')).$sj_spec_query.((!empty($sj_spec_query) ? '&' : '')).'page='.$page));
			$sj = $this->sj_api->process_vacancies($sj_answer);
			$sj_more = $sj_answer->more;
		} else {
			$sj = array();
			$sj_more = false;
		}
		// $hh_answer = json_decode($this->hh_api->make_request('vacancies?'.$hh_city_query.((!empty($hh_city_query) ? '&' : '')).$hh_spec_query.((!empty($hh_spec_query) ? '&' : '')).'page='.$page));
		// $sj_answer = json_decode($this->sj_api->make_request('vacancies/?'.$sj_city_query.((!empty($sj_city_query) ? '&' : '')).$sj_spec_query.((!empty($sj_spec_query) ? '&' : '')).'page='.$page));
		// $hh = $this->hh_api->process_vacancies($hh_answer);
		// $sj = $this->sj_api->process_vacancies($sj_answer);
		$this->response((object)array('items' => array_merge($hh,$sj),'sj_more' => $sj_more, 'hh_more' => $hh_more));
		 // print_r(array_merge($hh,$sj));
		// print('vacancies/?'.$sj_city_query.((!empty($sj_city_query) ? '&' : '')).$sj_spec_query.((!empty($sj_spec_query) ? '&' : '')).'page='.$page);
	}
	public function vacancies2_get() { // Получение вакансий
		if($this->get('page')) {
			$page = $this->get('page');
		} else {
			$page = 0;
		}
		$hh_city_query = '';
		$sj_city_query = '';
		$hh_spec_query = '';
		$sj_spec_query = '';
		$this->load->model('hh_api');
		$this->load->model('sj_api');
		if($this->get('city')) { // Если заданы города
			$cities = $this->get('city');
			$this->load->helper('array');
			$this->load->model('city');
			$hh_cities = array();
			$sj_cities = array();
			foreach($cities as $city) { // Получем из базы ID городов внутри систем SJ/HH
				$city_obj = element(0, $this->city->where('id',$city)->get()->all_to_array());
				if ($city_obj['hh_id']!=0) {
					$hh_cities[] = $city_obj['hh_id'];
				}
				if ($city_obj['sj_type'] && $city_obj['sj_id']) {
					$sj_cities[] = array('id' => $city_obj['sj_id'],'type' => $city_obj['sj_type']);
				}
			}
			//Составляем части для запросов
			$hh_city_query = $this->hh_api->create_query_part($hh_cities,'area');
			
			$first = true;
			foreach($sj_cities as $sj_city) {
				if (!$first) {
					$sj_city_query .='&';
				}
				$sj_city_query .= $sj_city['type'].'[]='.$sj_city['id'];
				$first = false;
			}
		}
		//Если заданы категории для HH - составляем часть запроса
		$hhcats = array();
		$sjcats = array();
		if ($cats = $this->get('cats')) {
			$this->load->model('site_source_categories');
			$query=$this->site_source_categories;
			$first = true;
			foreach($cats as $cat) {
				if ($first) {
					$query = $query->where('Site_id',$cat);
					$first = false;
				} else {
					$query = $query->or_where('Site_id',$cat);
				}
			}
			$out = $query->get()->all_to_array();
			if (!empty($out)) {
				$this->load->model('sourcecategories');
				$query=$this->sourcecategories;
				$first = true;
				foreach($out as $out_row) {
					if ($first) {
						$query = $query->where('id',$out_row['Source_id']);
						$first = false;
					} else {
						$query = $query->or_where('id',$out_row['Source_id']);
					}
				}
				$out = $query->get()->all_to_array();
				foreach($out as $out_row) {
					if ($out_row['site']=='hh') {
						$hhcats[] = $out_row['site_id'];
					}
					if ($out_row['site']=='sj') {
						$sjcats[] = $out_row['site_id'];
					}
				}
			}
		}
		// print_r($hhcats);
		// print_r($sjcats);
		if(!empty($hhcats)) {
			$hh_spec_query = $this->hh_api->create_query_part($hhcats,'specialization');
			log_message('info','vacancies?'.$hh_city_query.((!empty($hh_city_query) ? '&' : '')).$hh_spec_query.((!empty($hh_spec_query) ? '&' : '')).'page='.$page);
			$hh_answer = json_decode($this->hh_api->make_request('vacancies?'.$hh_city_query.((!empty($hh_city_query) ? '&' : '')).$hh_spec_query.((!empty($hh_spec_query) ? '&' : '')).'page='.$page));
			$hh = $this->hh_api->process_vacancies($hh_answer);
			$hh_more = ($hh_answer->pages-2>=$hh_answer->page);
		} else {
			$hh = array();
			$hh_more = false;
		}
		if(!empty($sjcats)) {
			$sj_spec_query = 'catalogues='.implode(",", $sjcats);
			$sj_answer = json_decode($this->sj_api->make_request('vacancies/?'.$sj_city_query.((!empty($sj_city_query) ? '&' : '')).$sj_spec_query.((!empty($sj_spec_query) ? '&' : '')).'page='.$page));
			$sj = $this->sj_api->process_vacancies($sj_answer);
			$sj_more = $sj_answer->more;
		} else {
			$sj = array();
			$sj_more = false;
		}
		$this->response((object)array('items' => array_merge($hh,$sj),'sj_more' => $sj_more, 'hh_more' => $hh_more));
	}
	public function addbookmark_get() {
		$this->check_if_user();
		if(!$this->get('site') && ($this->get('site')!='sj' && $this->get('site')!='hh') && !($this->get('site_id'))) {
			$this->response(array('status' => 'Bad request'), 400);
		}
		$this->load->model('bookmark');
		$user_samebookmark = $this->bookmark->where('user_id',$this->get('user_id'))->where('site',$this->get('site'))->where('site_id',$this->get('site_id'))
		->where('deleted IS NULL')->get()->all_to_array();
		if (!empty($user_samebookmark)) {
			$this->response(array('status' => 'Already bookmarked'), 400);
			return false;
		}
		if ($this->get('site')=='sj') {
			$this->load->model('sj_api');
			$bookmark = new Bookmark;
			$obj = $this->sj_api->process_vacansy(json_decode($this->sj_api->make_request('vacancies/'.$id.'/')));
			$bookmark->user_id = $this->get('user_id');
			foreach((array)$obj as $obj_key=>$obj_value) {
				$bookmark->$obj_key=$obj_value;
			}
			if ($bookmark->save()) {
				$this->response(array('status' => 'success'), 200);
			} else {
				$this->response(array('status' => 'fail','error' => $bookmark->error->string));
			}
		}
		if ($this->get('site')=='hh') {
			$this->load->model('hh_api');
			$bookmark = new Bookmark;
			$obj = $this->hh_api->process_vacansy(json_decode($this->hh_api->make_request('vacancies/'.$id)));
			$bookmark->user_id = $this->get('user_id');
			foreach((array)$obj as $obj_key=>$obj_value) {
				$bookmark->$obj_key=$obj_value;
			}
			if ($bookmark->save()) {
				$this->response(array('status' => 'success'), 200);
			} else {
				$this->response(array('status' => 'fail','error' => $bookmark->error->string));
			}
		}
	}
	
	public function bookmarks_get() {
		$this->check_if_user();
		$this->load->model('bookmark');
		$bookmarks = $this->bookmark->where('user_id',$this->get('user_id'))->where('deleted IS NULL')->get()->all_to_array();
		$this->response($bookmarks, 200);
	}
	
	public function bookmarkdelete_get() {
		$this->check_if_user();
		if(!$this->get('bookmark_id')) {
			$this->response(array('status' => 'Bad request'), 400);
		}
		$this->load->model('bookmark');
		$user_bookmark = $this->bookmark->where('user_id', $this->get('user_id'))->where('id',$this->get('bookmark_id'))->get()->all_to_array();
		if (empty($user_bookmark)) {
			$this->response(array('status' => 'Invalid bookmark'), 400);
			return false;
		}
		$user_bookmark = $this->bookmark->where('id',$this->get('bookmark_id'))->get();
		$user_bookmark->deleted = 1;
		if ($user_bookmark->save()) {
			$this->response(array('status' => 'success'));
		} else {
			$this->response(array('status' => 'fail', 'error' => $user_bookmark->error->string));
		}
	}
	public function subscribe_get() {
		$this->check_if_user();
		$this->load->model('subscription');
		$user_id = $this->get('user_id');
		foreach(array('work','city','category') as $type) {
			if (($values = $this->get($type)) && is_array($values)) {
				foreach($values as $value) {
					$this->subscription->add_subscription($user_id,$type,$value);
				}
			}
		}
		$this->response(array('status' => 'success'));	
	}
	public function enable_subscription_get() {
		$this->check_if_user();
		$this->load->model('subscription');
		$user_id = $this->get('user_id');
		$this->subscription->make_subscriber($user_id);
		$this->response(array('status' => 'success'));
	}
	public function unsubscribe_get() {
		$this->check_if_user();
		$this->load->model('subscription');
		$user_id = $this->get('user_id');
		foreach(array('work','city','category') as $type) {
			if (($values = $this->get($type)) && is_array($values)) {
				foreach($values as $value) {
					$this->subscription->remove_subscription($user_id,$type,$value);
				}
			}
		}
		$this->response(array('status' => 'success'));	
	}
	private function check_if_user() {
		if(!$this->get('user_id') && !$this->get('apikey')) {
			$this->response(array('status' => 'Bad request'), 400);
			die;
		}
		$this->load->model('user');
		$user_apikey = $this->user->where('id',$this->get('user_id'))->where('apikey',$this->get('apikey'))->get()->all_to_array();
		if (empty($user_apikey)) {
			$this->response(array('status' => 'Invalid user'), 400);
			die;
		}
		return true;
	}
	public function worktypes_get() {
		$this->load->model('worktype');
		$this->response($this->worktype->get()->all_to_array(),400);
	}
	public function add_gcmid_get() {
		$this->check_if_user();
		$user_id = $this->get('user_id');
		if ($reg_id = $this->get('gcmid')) {
			$this->load->model('subscription');
			if ($this->subscription->add_gcmid($user_id,$reg_id)) {
				$this->response(array('status' => 'success'), 400);
			} else {
				$this->response(array('status' => 'Invalid id'), 400);
			}
		} else {
			$this->response(array('status' => 'No reg id'), 400);
		}
	}
} 