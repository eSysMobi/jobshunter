<?php

class User extends DataMapper {
	var $model = 'user';
	var $table = 'users';
	
	var $has_one = array();
	var $has_many = array();

	var $validation = array(
		'network' => array(
			'rules' => array('required', 'max_length' => 1, 'min_length' => 1,'valid_match' => array('f', 'v')),
			'label' => 'Network'
		),
		'network_id' => array(
			'rules' => array('required'),
			'label' => 'network_id'
		),
		'first_name' => array(
			'rules' => array('required', 'max_length' => 20, 'min_length' => 1),
			'label' => 'First Name'
		),
		'last_name' => array(
			'rules' => array('required', 'max_length' => 20, 'min_length' => 1),
			'label' => 'Last Name'
		),
		'apikey' => array(
			'rules' => array('required'),
			'label' => 'Apikey'
		),
		'deleted' => array(
			'rules' => array('valid_match' => array(NULL, 1)),
			'label' => 'Deleted'
		)
	);

	var $default_order_by = array('id' => 'desc');
 

    function __construct($id = NULL)
	{
		parent::__construct($id);
    }

	function post_model_init($from_cache = FALSE)
	{
	}

	public function generate_apikey($length=15) {
		$key = '';
		$keys = array_merge(range(0, 9), range('a', 'z'));

		for ($i = 0; $i < $length; $i++) {
			$key .= $keys[array_rand($keys)];
		}

		return $key;
	}
	
	function login()
    {
        $u = new User();
        $u->where('network', $this->network)->where('network_id', $this->network_id)->get();
        $this->validate()->get();
        if (empty($this->id))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}