# List of implemented test

-	it.polimi.ingsw.model
	-	Dice
		*	constructor
		*	isEqual(Dice d)
		*	isSimilar (Dice d)
	-	Placement
		*	constructor
		*	isEqual(Dice d)
		*	isSimilar (Dice d)
	-	Cell
		*	constructor
		*	setDice(Dice d)
	-	Dadiera
		*	constructor
		*	mix(int n)
		*	deleteDice()
		*	getDice() (With Exception and without Exception)
	-	DiceBag
		*	constructor
		*	pickDices(n)
	-	Window
		*	constructor
		*	addDice(int x,int y,Dice d)
-	it.polimi.ingsw.server
	-	TokenTurn
		*	constructor
		*	addPlayer(String s)
		*	deletePlayer(string s)
		*	isMyTurn()
		*	nextTurn()

-	it.polimi.ingsw.objectives
	-	ColRowScore
		*	calcScore(int value, Cell[][] grid)
			*	case with expected result of zero (both for colors and shade)
			*	case with expected result different from zero (both for colors and shade)
	-	PairScore
		*	calcScore(int value, Cell[][] grid)
			*	case with expected result of zero (test with no expected pairs placed)
			*	case with expected result different from zero (for all possible pairs)
	-	VarietyScore
		*	calcScore(int value, Cell[][] grid)
			*	case with expected result of zero (test with no expected cluster (both colors and values) placed)
			*	case with expected result different from zero (for clusters of all possible size (both colors and values))
	-	PrivateObjective
		*	getName()
			*	assertNotNull test
		*	getDescription()
			*	assertNotNull test
	-	ObjectivevesFactory
		*	getPrivateObjective(String path)
			*	check the type of the expected result
		*	getPublicObjective(String path)
			*	check the type of the expected result
