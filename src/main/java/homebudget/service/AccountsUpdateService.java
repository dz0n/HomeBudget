package homebudget.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import homebudget.domain.Account;
import homebudget.domain.Receipt;
import homebudget.domain.Subcategory;

@Service
public class AccountsUpdateService {
	private final ReceiptService receiptService;
	
	private final static String defaultName = "Aktualizacja wartości";
	
	@Autowired
	public AccountsUpdateService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public void updateAccount(Account account, BigDecimal properValue, Subcategory subcategory) {
		BigDecimal difference = properValue.subtract(account.getValue());
		if(difference.doubleValue()!=0)
			receiptService.save(new Receipt(defaultName, difference, account, subcategory));
	}
}
