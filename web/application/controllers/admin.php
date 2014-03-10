<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Admin extends CI_Controller {
	private $view_data;
	private function check_if_admin($error=true) {
		if ($this->session->userdata('logged_in') && $this->session->userdata('type')=='admin') {
			return true;
		}
		if (!$error) {
			return false;
		}
		redirect('/admin/login_page/', 'location');
		die;
	}
	public function index() {
		$this->check_if_admin();
		$this->view_data['content'] = 'index';
		$this->prepare_nav_menu();
		$this->prepare_main_menu();
		$this->open_view();
	}
	public function categories() {
		$this->check_if_admin();
		$this->view_data['content'] = 'categories';
		$this->load->model('sourcecategories');
		$this->load->helper('form');
		$this->load->helper('tree');
		$this->load->model('site_categories');
		$this->load->model('site_source_categories');
		$matches = $this->site_source_categories->get()->all_to_array();
		foreach($matches as $match) {
			$matches_mod[$match['Site_id']][]=$match['Source_id'];
		}
		// setMatches
		$all_cats = $this->site_categories->get()->all_to_array();
		$sj_cats = $this->sourcecategories->where('site', 'sj')->get()->all_to_array();
		$tree = new Tree();
		foreach($all_cats as &$cat) {
			$cat['match'] = (isset($matches_mod[$cat['id']])?$matches_mod[$cat['id']]:array());
			$tree->addItem($cat['id'], $cat['parent_id'],$cat);
		}
		$sj_tree = new Tree;
		foreach($sj_cats as $sj_cat) {
			$sj_tree->addItem($sj_cat['id'], $sj_cat['parent_id'],$sj_cat);
		}
		$tree->setSjCats($sj_tree);
		$this->view_data['sj_tree'] = $sj_tree;
		$this->view_data['cat_tree'] = $tree;
		$this->prepare_nav_menu();
		$this->prepare_main_menu();
		$this->open_view();
	}
	public function login() {
		$this->load->helper('form');
		$this->load->model('site_user');
		$this->load->library('form_validation');
		$this->form_validation->set_rules('login', 'Username', 'required|xss_clean|trim');
		$this->form_validation->set_rules('pass', 'Password', 'required|xss_clean|trim');
		if ($this->form_validation->run() == false)	{
			show_error('Неправильные пароль или логин');
		} else {
			$login = $this->input->post('login');
			$pass = $this->input->post('pass');
			if ($this->site_user->login($login,$pass)) {
				redirect('/admin/index/', 'location');
			} else {
				show_error('Неправильные пароль или логин');
			}
		}
	}
	public function login_page() {
		if ($this->check_if_admin(false)) {
			show_error('Вы уже зашли.');
		}
		$this->view_data['content'] = 'login';
		$this->prepare_nav_menu();
		$this->prepare_main_menu();
		$this->open_view();
	}
	
	
	private function open_view() {
		$this->load->view('main',$this->view_data);
	}
	private function prepare_nav_menu() {
		if ($this->view_data['content']=='login') {
			$this->view_data['nav_menu'] = array(array('url' => site_url('admin/login'), 'name' => 'Вход'));
		}
		$this->view_data['nav_menu'][] = array('url' => site_url('admin/index'), 'name' => 'Главная');
		if ($this->view_data['content']=='categories') {
			$this->view_data['nav_menu'][] = array('url' => site_url('admin/categories'), 'name' => 'Категории');
		}
		if ($this->view_data['content']=='settings') {
			$this->view_data['nav_menu'][] = array('url' => site_url('admin/settings'), 'name' => 'Настройки');
		}
		
	}
	private function prepare_main_menu() {
		if ($this->view_data['content']=='login') {
			$this->view_data['main_menu'] = array(array('url' => site_url('admin/login'), 'name' => 'Вход','is_active' => true));
		} else {
			$menus = array(
				'index' => array('name' => 'Главная','url' => site_url('admin/index'), 'is_active' => false),
				'categories' => array('name' => 'Категории','url' => site_url('admin/categories'), 'is_active' => false),
				'settings' => array('name' => 'Настройки','url' => site_url('admin/settings'), 'is_active' => false),
			);
			if ($this->view_data['content']) {
				$menus[$this->view_data['content']]['is_active'] = true;
			}
			$this->view_data['main_menu'] = $menus;
		}
	}
	public function update_category() {
		$this->check_if_admin();
		$this->load->library('form_validation');
		$this->load->model('site_source_categories');
		$this->load->library('user_agent');
		$site_id = $this->input->get('id');
		if (!$site_id || !is_numeric($site_id) ) {
			show_error('Invalid id');
		}
		$matches = $this->input->post('catid');
		$db_matches = $this->site_source_categories->where('Site_id', $site_id)->get()->all_to_array();
		$db_matches_mod = array();
		foreach($db_matches as $db_matches_row) {
			$db_matches_mod[$db_matches_row['Source_id']] = true;
		}
		$db_matches = $db_matches_mod;
		foreach($matches as $match) {
			if (!isset($db_matches[$match])) {
				$row = new site_source_categories();
				$row->Site_id = $site_id;
				$row->Source_id = $match;
				$row->save();
			} else {
				unset($db_matches[$match]);
			}
		}
		if (!empty($db_matches)) {
			foreach($db_matches as $source_id=>$tmp) {
				$this->db->delete('site_source_categories', array('Source_id' => $source_id));
			}
		}
		redirect('admin/categories','location');
	}
	public function settings() {
		$this->check_if_admin();
		$this->view_data['content'] = 'settings';
		$this->view_data['settings'] = new settings;
		$this->prepare_nav_menu();
		$this->prepare_main_menu();
		$this->open_view();
	}
	public function save_settings() {
		foreach(array('parse_days','cur_uah','cur_byr','cur_usd','cur_eur','cur_azn','cur_kzt') as $var) {
			$this->settings->$var = $this->input->post($var);
		}
		$this->settings->save();
		redirect('admin/settings','location');
 	}
}