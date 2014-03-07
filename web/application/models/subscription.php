<?
class subscription extends CI_Model {
	function __construct()
	{
		parent::__construct();
	}
	function add_subscription($user_id,$type,$sub_id) {
		$this->make_subscriber($user_id);
		if (in_array($type, array('work','city','category'))) {
			if ($this->db->select('user_id')->from('subscriptions')->where('user_id', $user_id)->where('sub_type', $type)->where('sub_id', $sub_id)->count_all_results()<0.5) {
				$this->db->insert('subscriptions', array('user_id' => $user_id,'sub_type' => $type,'sub_id' => $sub_id));
			}
			return 'success';
		} else {
			return 'undefined type';
		}
	}
	function make_subscriber($user_id) {
		if ($this->db->select('id')->from('users')->where('id', $user_id)->where('subscriber', 1)->count_all_results()<0.5) {
			$this->db->where('id', $user_id)->update('users', array('subscriber' => 1));
		}
	}
	function unsubscribe($user_id) {
		$this->db->where('id', $user_id)->update('users', array('subscriber' => 0));
	}
}